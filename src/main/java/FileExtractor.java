import bayes.IBayesEstimator;
import bayes.TfidfBayesEstimator;
import com.google.common.base.Stopwatch;
import io.IDBConfiguration;
import io.IDBLoadingTask;
import io.LemmaEntityMapper;
import io.MongoLoadingPipe;
import io.config.ConfigHandler;
import io.config.GsonApplicationConfig;
import io.simple.SimpleDBConfiguration;
import io.simple.SimpleDBCredentials;
import io.simple.SimpleDBLoadingTask;
import learn.IRelationClassifier;
import learn.RCDefaultFactory;
import learn.VotingRelationFactory;
import model.IConfiguration;
import model.ILabelledEntity;
import model.Relation;
import pipeline.IPipe;
import pipeline.SingleCachedSink;
import preprocessing.string.WriteOutSink;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 *
 * A relation extraction application which allows to provide a input file.
 * The input file will be parsed and relation will be extracted for each line.
 *
 * The relations will be written in a given ouput file.
 *
 * arguments:
 *  0: Setup configuration path (can be invalid)
 *  1: Input file path with sentences
 *  2: Output file path for extracted relations (in Turtle format)
 *
 * @author Cedric Richter
 */
public class FileExtractor {

    public static void main(String[] args) throws IOException {

        String path = "./system_config.json";
        String inPath = null , outPath = null;

        if(args.length > 0){
            path = args[0];
            inPath = args[1];
            outPath = args[2];
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

        IPipe<ILabelledEntity, IRelationClassifier> trainModelPipe =
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
        IDBConfiguration configuration = handler.getConfig("mongo", new SimpleDBConfiguration(
                "ds113738.mlab.com",  13738,
                new SimpleDBCredentials("admin", "admin".toCharArray() ),
                "relationextraction"
        ));

        IDBLoadingTask trainTask =
                new SimpleDBLoadingTask(configuration, "DataSet", null);

        System.out.println("Start training phase: ");

        Stopwatch watch = Stopwatch.createStarted();
        trainLoader.push(trainTask);
        watch.stop();

        System.out.println("Time for Training: "+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");

        IRelationClassifier classificator = classificatorSink.getObj();

        IPipe<String, Relation> processor = factory.createRelationExtractionPipe(classificator, window);
        processor.setSink(new WriteOutSink(
                new PrintStream(outPath)
        ));

        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(inPath))));

            br.lines().forEach(x -> {System.out.println("Process: "+x); processor.push(x);});

        }  finally {
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
