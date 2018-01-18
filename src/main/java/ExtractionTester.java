import bayes.BayesTrainPipe;
import bayes.IBayesEstimator;
import bayes.TfidfBayesEstimator;
import com.google.common.base.Stopwatch;
import io.IDBConfiguration;
import io.IDBLoadingTask;
import io.LemmaEntityMapper;
import io.MongoLoadingPipe;
import io.simple.SimpleDBConfiguration;
import io.simple.SimpleDBCredentials;
import io.simple.SimpleDBLoadingTask;
import javafx.util.Pair;
import learn.*;
import learn.validate.ClassificationTestingPipe;
import learn.validate.IClassificationTestResult;
import model.ILabelledEntity;
import model.ITrainingData;
import pipeline.IPipe;
import pipeline.MergePipe;
import pipeline.SingleCachedSink;
import preprocessing.FilterPreprocessor;
import preprocessing.RelationCollector;
import preprocessing.StopwordPreprocessor;
import preprocessing.WindowPreprocessor;
import preprocessing.filter.PredicateFilter;
import preprocessing.string.NERStringPreprocessor;
import preprocessing.string.PrintOutSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ExtractionTester {

    public static void main(String[] args){

        int window = 2;
        int k = 5;

        IBayesEstimator estimator = new TfidfBayesEstimator(1.0);


        //Train Pipe
        SingleCachedSink<IRelationClassificator> classificatorSink = new SingleCachedSink<>();

        IPipe<ITrainingData, IRelationClassificator> trainModelPipe = new RelationTrainPipe(
                new BayesRelationFactory(
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
                new SimpleDBLoadingTask(configuration, "DataSet", null);

        System.out.println("Start training phase: ");

        Stopwatch watch = Stopwatch.createStarted();
        trainLoader.push(trainTask);
        watch.stop();

        System.out.println("Time for Training: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");

        IRelationClassificator classificator = classificatorSink.getObj();

        MergePipe<Pair<String, String>, IClassificationTestResult> merge = new MergePipe<>();

        IPipe<ILabelledEntity, IClassificationTestResult> testResultIPipe = new ClassificationTestingPipe(
                classificator
        );
        testResultIPipe.setSink(merge.getSecondSink());

        windowPreprocessor = new WindowPreprocessor(window);
        windowPreprocessor.setSink(testResultIPipe);

        stopPreprocessor = new StopwordPreprocessor();
        stopPreprocessor.setSink(windowPreprocessor);

        NERStringPreprocessor stringProcessor = new NERStringPreprocessor();
        stringProcessor.setSink(stopPreprocessor);
        stringProcessor.setTagSink(merge.getFirstSink());

        merge.setSink(new PrintOutSink());

        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {

                System.out.print("Enter something : ");
                String input = br.readLine();

                if ("q".equals(input)) {
                    System.out.println("Exit!");
                    System.exit(0);
                }

                System.out.println("Processing input: "+input);
                stringProcessor.push(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }





    }

}
