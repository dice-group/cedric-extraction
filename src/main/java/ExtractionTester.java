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
import learn.validate.IClassificationTestResult;
import model.ILabelledEntity;
import model.Relation;
import pipeline.IPipe;
import pipeline.MergePipe;
import pipeline.SingleCachedSink;
import preprocessing.FilterPreprocessor;
import preprocessing.filter.PredicateFilter;
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

        RelationExtPipeFactory factory = new RelationExtPipeFactory();


        //Train Pipe
        SingleCachedSink<IRelationClassificator> classificatorSink = new SingleCachedSink<>();

        IPipe<ILabelledEntity, IRelationClassificator> trainModelPipe =
                factory.createTrainingPipe(window, estimator,
                        new VotingRelationFactory(
                                new RCDefaultFactory(k, estimator)
                        ));
        trainModelPipe.setSink(classificatorSink);

        IPipe<IDBLoadingTask, ILabelledEntity>  trainLoader = new MongoLoadingPipe(
                new LemmaEntityMapper()
        );
        trainLoader.setSink(trainModelPipe);


        //Setup train data loading
        IDBConfiguration configuration = new SimpleDBConfiguration(
                "ds113738.mlab.com",  13738,
                new SimpleDBCredentials("admin", "admin".toCharArray() ),
                "relationextraction"
        );

        IDBLoadingTask trainTask =
                new SimpleDBLoadingTask(configuration, "DataSet", null);

        System.out.println("Start training phase: ");

        Stopwatch watch = Stopwatch.createStarted();
        trainLoader.push(trainTask);
        watch.stop();

        System.out.println("Time for Training: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");

        IRelationClassificator classificator = classificatorSink.getObj();

        IPipe<String, Relation> processor = factory.createRelationExtractionPipe(classificator, window);
        processor.setSink(new PrintOutSink());

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
                processor.push(input);
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
