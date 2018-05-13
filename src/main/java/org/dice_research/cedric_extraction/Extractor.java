package org.dice_research.cedric_extraction;

import org.dice_research.cedric_extraction.bayes.IBayesEstimator;
import org.dice_research.cedric_extraction.bayes.TfidfBayesEstimator;
import com.google.common.base.Stopwatch;
import org.dice_research.cedric_extraction.io.IDBConfiguration;
import org.dice_research.cedric_extraction.io.IDBLoadingTask;
import org.dice_research.cedric_extraction.io.LemmaEntityMapper;
import org.dice_research.cedric_extraction.io.MongoLoadingPipe;
import org.dice_research.cedric_extraction.io.config.ConfigHandler;
import org.dice_research.cedric_extraction.io.config.GsonApplicationConfig;
import org.dice_research.cedric_extraction.io.simple.SimpleDBConfiguration;
import org.dice_research.cedric_extraction.io.simple.SimpleDBCredentials;
import org.dice_research.cedric_extraction.io.simple.SimpleDBLoadingTask;
import org.dice_research.cedric_extraction.learn.IRelationClassifier;
import org.dice_research.cedric_extraction.learn.RCDefaultFactory;
import org.dice_research.cedric_extraction.learn.VotingRelationFactory;
import org.dice_research.cedric_extraction.model.IConfiguration;
import org.dice_research.cedric_extraction.model.ILabelledEntity;
import org.dice_research.cedric_extraction.model.Relation;
import org.dice_research.cedric_extraction.pipeline.IPipe;
import org.dice_research.cedric_extraction.pipeline.SingleCachedSink;
import org.dice_research.cedric_extraction.preprocessing.string.WriteOutSink;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Kevin Haack
 */
public class Extractor {
	
	private final static Charset CHARTSET = StandardCharsets.UTF_8;

	private final static String CONFIG_KEY_LEANER = "learner";

	private final static String DATABASE_HOSTNAME = "ds113738.mlab.com";

	private final static int DATABASE_PORT = 13738;
	
	private final static String DATABASE = "relationextraction";

	private static ConfigHandler handler;

	private static IConfiguration base;
	
	private static SingleCachedSink<IRelationClassifier> classificatorSink;

	private Extractor() {

	}

	public static void setup() {
		String path = "./system_config.json";
		ConfigHandler.injectCustomConfig(ExtractionTester.class.getSimpleName(), Paths.get(path));
		Extractor.handler = ConfigHandler.createCustom(ExtractionTester.class.getSimpleName());
		
		Extractor.classificatorSink = new SingleCachedSink<>();
	}

	public static void train() {
		Extractor.base = handler.getConfig(Extractor.CONFIG_KEY_LEANER, new GsonApplicationConfig());

		int window = base.getLexicalWindow();
		int k = base.getNeighbour();

		IBayesEstimator estimator = new TfidfBayesEstimator(Extractor.base.getBayesSmoothing());
		RelationExtPipeFactory factory = new RelationExtPipeFactory();

		// Train Pipe
		VotingRelationFactory votingRelationFactory = new VotingRelationFactory(new RCDefaultFactory(k, estimator));
		IPipe<ILabelledEntity, IRelationClassifier> trainModelPipe = factory.createTrainingPipe(window, estimator, votingRelationFactory);
		trainModelPipe.setSink(classificatorSink);

		IPipe<IDBLoadingTask, ILabelledEntity> trainLoader = new MongoLoadingPipe(new LemmaEntityMapper());
		trainLoader.setSink(trainModelPipe);

		// Setup train data loading
		SimpleDBCredentials credentials = new SimpleDBCredentials("admin", "admin".toCharArray());
		SimpleDBConfiguration simpleDBConfiguration = new SimpleDBConfiguration(Extractor.DATABASE_HOSTNAME, Extractor.DATABASE_PORT, credentials, Extractor.DATABASE);
		IDBConfiguration configuration = handler.getConfig("mongo", simpleDBConfiguration);
		
		IDBLoadingTask trainTask = new SimpleDBLoadingTask(configuration, "DataSet", null);

		trainLoader.push(trainTask);
	}
	
	public static String extract(String input) {
		Extractor.base = handler.getConfig(Extractor.CONFIG_KEY_LEANER, new GsonApplicationConfig());

		int window = base.getLexicalWindow();
		
		IRelationClassifier classificator = Extractor.classificatorSink.getObj();
		
		RelationExtPipeFactory factory = new RelationExtPipeFactory();
		IPipe<String, Relation> processor = factory.createRelationExtractionPipe(classificator, window);
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintStream outPrintStream = new PrintStream(outStream);
		WriteOutSink outSink = new WriteOutSink(outPrintStream);
		processor.setSink(outSink);
		
		BufferedReader br = null;
		InputStream inStream = new ByteArrayInputStream(input.getBytes(CHARTSET));
		
		// extract
		try {
			
			br = new BufferedReader(new InputStreamReader(inStream));

			br.lines()
			  .forEach(x -> {
				  processor.push(x);
			  });

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// read from stream
		String result = new String(outStream.toByteArray(), Extractor.CHARTSET);
		
		return result;
	}
}
