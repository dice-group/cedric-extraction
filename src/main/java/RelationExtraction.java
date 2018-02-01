import bayes.IBayesEstimator;
import bayes.TfidfBayesEstimator;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import io.IDBConfiguration;
import io.IDBLoadingTask;
import io.LemmaEntityMapper;
import io.MongoLoadingPipe;
import io.config.ConfigHandler;
import io.config.GsonApplicationConfig;
import io.simple.SimpleDBConfiguration;
import io.simple.SimpleDBCredentials;
import io.simple.SimpleDBLoadingTask;
import learn.*;
import learn.validate.*;
import model.IConfiguration;
import model.ILabelledEntity;
import pipeline.IPipe;
import pipeline.SingleCachedSink;
import preprocessing.RelationCollector;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * A programm which trained a model on training data and then test it
 * on a set of test entities.
 *
 * As a result:
 *  F1 measure for each relation
 *  Precision for each relation
 *  Recall for each relation
 *  Precision for all relations together
 *
 * @author Cedric Richter
 */
public class RelationExtraction {

    public static void main(String[] args){

        String path = "./system_config.json";

        if(args.length > 0){
            path = args[0];
        }

        ConfigHandler.injectCustomConfig(ExtractionTester.class.getSimpleName(), Paths.get(path));
        ConfigHandler handler = ConfigHandler.createCustom(ExtractionTester.class.getSimpleName());
        IConfiguration base = handler.getConfig("learner", new GsonApplicationConfig());

        int window = base.getLexicalWindow();
        int k = base.getNeighbour();

        IBayesEstimator estimator = new TfidfBayesEstimator(base.getBayesSmoothing());

        RelationExtPipeFactory factory = new RelationExtPipeFactory();


        //Train Pipe
        SingleCachedSink<IRelationClassifier> classificatorSink = new SingleCachedSink<>();

        IPipe<ILabelledEntity, IRelationClassifier> trainModelPipe  = factory.createTrainingPipe(window,estimator,
                new VotingRelationFactory(
                        new RCDefaultFactory(k, estimator)
                ));
        trainModelPipe.setSink(classificatorSink);

        RelationCollector relationColl = new RelationCollector();
        relationColl.setSink(trainModelPipe);

        IPipe<IDBLoadingTask, ILabelledEntity>  trainLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        trainLoader.setSink(relationColl);


        //Setup train data loading
        IDBConfiguration configuration = handler.getConfig("mongo", new SimpleDBConfiguration(
               "ds113738.mlab.com",  13738,
                new SimpleDBCredentials("admin", "admin".toCharArray() ),
                "relationextraction"
        ));


        IDBLoadingTask trainTask =
                new SimpleDBLoadingTask(configuration, "TrainingDataSet", null);

        System.out.println("Start training phase: ");

        Stopwatch watch = Stopwatch.createStarted();
        trainLoader.push(trainTask);
        watch.stop();

        System.out.println("Time for Training: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");


        //Typical words
        IRelationClassifier classificator = classificatorSink.getObj();

        if(classificator instanceof BayesRelationClassifier){
            Multimap<String, String> map = ((BayesRelationClassifier) classificator).getTypicalWordsPerRelation(10);
            System.out.println("Typical words: ");
            for(Map.Entry<String, Collection<String>> e: map.asMap().entrySet()){
                System.out.println(e.getKey()+": "+e.getValue());
            }

        }


        //Setup scores
        List<ITestScorer> scores = new ArrayList<>();

        for(String pred: relationColl.getRelations()) {
            scores.add(new PrecisionScore(pred));
            scores.add(new RecallScore(pred));
            scores.add(new F1Score(pred));
        }

        scores.add(new PerformanceScorer());


        //Test Pipe
        SingleCachedSink<ITestResult> performanceSink = new SingleCachedSink<>();

        IPipe<IClassificationTestResult, ITestResult> performancePipe = new PerformanceTestingPipe(
               scores
        );
        performancePipe.setSink(performanceSink);

        IPipe<ILabelledEntity, IClassificationTestResult> testResultIPipe = factory.createTestingPipe(
                classificatorSink.getObj(),
                window
        );
        testResultIPipe.setSink(performancePipe);

        IPipe<IDBLoadingTask, ILabelledEntity>  testLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        testLoader.setSink(testResultIPipe);

        IDBLoadingTask testTask =
                new SimpleDBLoadingTask(configuration, "TestDataSet", null);


        //Performance test
        System.out.println("Start testing phase: ");

        watch = Stopwatch.createStarted();
        testLoader.push(testTask);
        watch.stop();

        System.out.println("Time for Testing: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");

        ITestResult result = performanceSink.getObj();

        for(ITestScorer score: result.getScores()){
            System.out.println(score.getScoreName()+": "+result.getResults().get(score.getScoreName()));
        }
    }
}
