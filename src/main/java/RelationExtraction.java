import bayes.BayesTrainPipe;
import bayes.IBayesEstimator;
import bayes.TfidfBayesEstimator;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import io.IDBConfiguration;
import io.IDBLoadingTask;
import io.LemmaEntityMapper;
import io.MongoLoadingPipe;
import io.simple.SimpleDBConfiguration;
import io.simple.SimpleDBCredentials;
import io.simple.SimpleDBLoadingTask;
import learn.*;
import learn.validate.*;
import model.ILabelledEntity;
import model.ITrainingData;
import pipeline.IPipe;
import pipeline.SingleCachedSink;
import preprocessing.FilterPreprocessor;
import preprocessing.RelationCollector;
import preprocessing.StopwordPreprocessor;
import preprocessing.WindowPreprocessor;
import preprocessing.filter.PredicateFilter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RelationExtraction {

    public static void main(String[] args){

        int window = 3;
        int k = 5;

        IBayesEstimator estimator = new TfidfBayesEstimator(1.0);


        //Train Pipe
        SingleCachedSink<IRelationClassificator> classificatorSink = new SingleCachedSink<>();

        IPipe<ITrainingData, IRelationClassificator> trainModelPipe = new RelationTrainPipe(
                new VotingRelationFactory(
                        new RCDefaultFactory(k, estimator)
                )
        );
        trainModelPipe.setSink(classificatorSink);


        IPipe<ILabelledEntity, ITrainingData> trainTransformPipe = new TrainingDataPipe();
        trainTransformPipe.setSink(trainModelPipe);

        IPipe<ILabelledEntity, ILabelledEntity> trainBayesPipe = new BayesTrainPipe(estimator);
        trainBayesPipe.setSink(trainTransformPipe);

        IPipe<ILabelledEntity, ILabelledEntity> windowPreprocessor = new WindowPreprocessor(window);
        windowPreprocessor.setSink(trainBayesPipe);

        IPipe<ILabelledEntity, ILabelledEntity> stopPreprocessor = new StopwordPreprocessor();
        stopPreprocessor.setSink(windowPreprocessor);

        RelationCollector relationColl = new RelationCollector();
        relationColl.setSink(stopPreprocessor);

        IPipe<ILabelledEntity, ILabelledEntity> filterPipe = new FilterPreprocessor(
                Arrays.asList(new PredicateFilter("residence"))
        );
        filterPipe.setSink(relationColl);

        IPipe<IDBLoadingTask, ILabelledEntity>  trainLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        trainLoader.setSink(filterPipe);


        //Setup train data loading
        IDBConfiguration configuration = new SimpleDBConfiguration(
               "ds231987.mlab.com",  31987,
                new SimpleDBCredentials("admin", "admin".toCharArray() ),
                "distantsupervision"
        );

        IDBLoadingTask trainTask =
                new SimpleDBLoadingTask(configuration, "TrainingDataSet", null);

        System.out.println("Start training phase: ");

        Stopwatch watch = Stopwatch.createStarted();
        trainLoader.push(trainTask);
        watch.stop();

        System.out.println("Time for Training: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");


        //Typical words
        IRelationClassificator classificator = classificatorSink.getObj();

        if(classificator instanceof VotingRelationClassificator){
            Multimap<String, String> map = ((VotingRelationClassificator) classificator).getTypicalWordsPerRelation(10);
            System.out.println("Typical words: ");
            for(Map.Entry<String, Collection<String>> e: map.asMap().entrySet()){
                System.out.println(e.getKey()+": "+e.getValue());
            }

        }


        //Setup scores
        List<ITestScorer> scores = new ArrayList<>();

        for(String pred: relationColl.getRelations())
            scores.add(new F1Score(pred));

        scores.add(new PerformanceScorer());


        //Test Pipe
        SingleCachedSink<ITestResult> performanceSink = new SingleCachedSink<>();

        IPipe<IClassificationTestResult, ITestResult> performancePipe = new PerformanceTestingPipe(
               scores
        );
        performancePipe.setSink(performanceSink);

        IPipe<ILabelledEntity, IClassificationTestResult> testResultIPipe = new ClassificationTestingPipe(
            classificatorSink.getObj()
        );
        testResultIPipe.setSink(performancePipe);

        windowPreprocessor = new WindowPreprocessor(window);
        windowPreprocessor.setSink(testResultIPipe);

        stopPreprocessor = new StopwordPreprocessor();
        stopPreprocessor.setSink(windowPreprocessor);

        filterPipe.setSink(stopPreprocessor);

        IPipe<IDBLoadingTask, ILabelledEntity>  testLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        testLoader.setSink(filterPipe);

        IDBLoadingTask testTask =
                new SimpleDBLoadingTask(configuration, "TestDataSet", null);


        //Performance test
        System.out.println("Start testing phase: ");

        watch = Stopwatch.createStarted();
        testLoader.push(testTask);
        watch.stop();

        System.out.println("Time for Testing: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");

        ITestResult result = performanceSink.getObj();

        for(Map.Entry<String, Double> score: result.getResults().entrySet()){
            System.out.println(score.getKey()+": "+score.getValue());
        }
    }
}
