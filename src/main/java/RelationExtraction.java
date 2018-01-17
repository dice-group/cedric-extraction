import bayes.BayesTrainPipe;
import bayes.IBayesEstimator;
import bayes.TfidfBayesEstimator;
import io.IDBConfiguration;
import io.IDBLoadingTask;
import io.LemmaEntityMapper;
import io.MongoLoadingPipe;
import io.simple.SimpleDBConfiguration;
import io.simple.SimpleDBCredentials;
import io.simple.SimpleDBLoadingTask;
import learn.*;
import learn.validate.ClassificationTestingPipe;
import learn.validate.IClassificationTestResult;
import learn.validate.PerformanceTestingPipe;
import model.ILabelledEntity;
import model.ITrainingData;
import pipeline.IPipe;
import pipeline.SingleCachedSink;
import preprocessing.StopwordPreprocessor;
import preprocessing.WindowPreprocessor;

public class RelationExtraction {

    public static void main(String[] args){

        int window = 2;
        int k = 3;

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

        IPipe<IDBLoadingTask, ILabelledEntity>  trainLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        trainLoader.setSink(stopPreprocessor);


        //Setup train data loading
        IDBConfiguration configuration = new SimpleDBConfiguration(
               "ds231987.mlab.com",  31987,
                new SimpleDBCredentials("admin", "admin".toCharArray() ),
                "distantsupervision"
        );

        IDBLoadingTask trainTask =
                new SimpleDBLoadingTask(configuration, "TrainingDataSet", null);

        trainLoader.push(trainTask);

        //Test Pipe
        SingleCachedSink<Double> performanceSink = new SingleCachedSink<>();

        IPipe<IClassificationTestResult, Double> performancePipe = new PerformanceTestingPipe();
        performancePipe.setSink(performanceSink);

        IPipe<ILabelledEntity, IClassificationTestResult> testResultIPipe = new ClassificationTestingPipe(
            classificatorSink.getObj()
        );
        testResultIPipe.setSink(performancePipe);

        windowPreprocessor = new WindowPreprocessor(window);
        windowPreprocessor.setSink(testResultIPipe);

        stopPreprocessor = new StopwordPreprocessor();
        stopPreprocessor.setSink(windowPreprocessor);

        IPipe<IDBLoadingTask, ILabelledEntity>  testLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        testLoader.setSink(stopPreprocessor);

        IDBLoadingTask testTask =
                new SimpleDBLoadingTask(configuration, "TestDataSet", null, 100);


        //Performance test
        testLoader.push(testTask);

        System.out.println("Performance: "+(performanceSink.getObj()*100)+"%");
    }
}
