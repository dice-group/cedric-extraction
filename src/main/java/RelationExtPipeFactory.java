

import bayes.BayesTrainPipe;
import bayes.IBayesEstimator;
import io.IDBLoadingTask;
import io.IEntityMapper;
import io.MongoLoadingPipe;
import javafx.util.Pair;
import learn.*;
import learn.validate.ClassificationTestingPipe;
import learn.validate.IClassificationTestResult;
import model.ILabelledEntity;
import model.ITrainingData;
import model.Relation;
import model.RelationEntity;
import pipeline.APipe;
import pipeline.CompressPipe;
import pipeline.IPipe;
import pipeline.MergePipe;
import preprocessing.LabelPreprocessor;
import preprocessing.RelationPrefixPostprocessor;
import preprocessing.StopwordPreprocessor;
import preprocessing.WindowPreprocessor;
import preprocessing.string.NERStringPreprocessor;
import preprocessing.string.URIFinderPipe;

/**
 *
 * A factory for creating preconfigured pipes.
 *
 * @author Cedric Richter
 */
public class RelationExtPipeFactory {

    /**
     * Creates a new pipe which executes the given loading task by
     * emitting labelled entities.
     * @param mapper a mapper which maps BSON objects to labelled entities
     * @return a pipe for executing loading tasks
     */
    public IPipe<IDBLoadingTask, ILabelledEntity> createDBLoadingPipe(IEntityMapper mapper){
        return new MongoLoadingPipe(mapper);
    }

    /**
     * A training pipe which allows to train a relation classifier.
     * The pipe processes and saves input entities until a stop signal is triggered.
     * After this point it emits a trained classifier.
     *
     * The pipe can be reused with caution:
     *  The bayes estimator is not reset. Therefore it is trained by every chunk of
     *  training data.
     *
     * The pipe provides:
     *  Feature selection
     *  Lemmatization
     *  Stop word extraction
     *  Naive Bayes training
     *  Relation classifier creation
     *
     * @param windowWidth how many features before the first entity and behind the last entity should be selected
     * @param untrainedEstimator an untrained estimator
     * @param factory a factory for the relation classifier that should be trained
     * @return a pipe which allows to train a new classifier
     */
    public IPipe<ILabelledEntity, IRelationClassifier> createTrainingPipe(int windowWidth,
                                                                          IBayesEstimator untrainedEstimator,
                                                                          IRelationClassifierFactory factory){

        IPipe<ITrainingData, IRelationClassifier> trainModelPipe = new RelationTrainPipe(
                factory
        );


        IPipe<ILabelledEntity, ITrainingData> trainTransformPipe = new TrainingDataPipe();
        trainTransformPipe.setSink(trainModelPipe);

        IPipe<ILabelledEntity, ILabelledEntity> trainBayesPipe = new BayesTrainPipe(untrainedEstimator);
        trainBayesPipe.setSink(trainTransformPipe);

        IPipe<ILabelledEntity, ILabelledEntity> windowPreprocessor = new WindowPreprocessor(windowWidth);
        windowPreprocessor.setSink(trainBayesPipe);

        IPipe<ILabelledEntity, ILabelledEntity> stopPreprocessor = new StopwordPreprocessor();
        stopPreprocessor.setSink(windowPreprocessor);

        IPipe<ILabelledEntity, ILabelledEntity> labelPreprocessor = new LabelPreprocessor();
        labelPreprocessor.setSink(stopPreprocessor);

        return new CompressPipe<>(labelPreprocessor, trainModelPipe);
    }

    /**
     * A pipe which allow to test a given label entity.
     *
     * On input it transforms the labelled entity to a feature vector and
     * then uses the given classifier to predict a relation for the entity.
     *
     * The pipe provides:
     *  Feature selection
     *  Lemmatization
     *  Stop word extraction
     *  Prediction of relation
     *
     * The pipe can be reused.
     *
     * The input of labelled entity is selected for simplicity.
     * If the pipe should used for entity based on an unlabelled example
     * the label can be arbitrarily chosen. The prediction is not depended on the label.
     *
     * @param classificator the trained classifier to use
     * @param windowWidth how many features before the first entity and behind the last entity should be selected
     * @return a pipe which takes labelled entities and produces a relation prediction
     */
    public IPipe<ILabelledEntity, IClassificationTestResult> createTestingPipe(IRelationClassifier classificator,
                                                                               int windowWidth){
        IPipe<ILabelledEntity, IClassificationTestResult> testResultIPipe = new ClassificationTestingPipe(
                classificator
        );

        IPipe<ILabelledEntity, ILabelledEntity> windowPreprocessor = new WindowPreprocessor(windowWidth);
        windowPreprocessor.setSink(testResultIPipe);

        IPipe<ILabelledEntity, ILabelledEntity> stopPreprocessor = new StopwordPreprocessor();
        stopPreprocessor.setSink(windowPreprocessor);

        IPipe<ILabelledEntity, ILabelledEntity> labelPreprocessor = new LabelPreprocessor();
        labelPreprocessor.setSink(stopPreprocessor);

        return new CompressPipe<>(labelPreprocessor, testResultIPipe);
    }

    /**
     *
     * A complete pipe which can take an input sentence and
     * emit all extracted relation.
     *
     * The pipe provides:
     *  Entity tagging in sentence
     *  Entity linking with a web ontology
     *  Feature extraction
     *  Lemmatization
     *  Stop word extraction
     *  Prediction of relation
     *  Assembling of relation description
     *
     *
     * The pipe can be reused.
     *
     * @param classificator the trained classifier to use
     * @param windowWidth how many features before the first entity and behind the last entity should be selected
     * @return a pipe which takes a string and produces multiple predicted relations
     */
    public IPipe<String, Relation> createRelationExtractionPipe(IRelationClassifier classificator, int windowWidth){

        MergePipe<Pair<RelationEntity, RelationEntity>, IClassificationTestResult> merge = new MergePipe<>();

        IPipe<ILabelledEntity, IClassificationTestResult> testPipe = createTestingPipe(classificator, windowWidth);
        testPipe.setSink(merge.getSecondSink());

        URIFinderPipe uriPipe = new URIFinderPipe();
        uriPipe.setSink(merge.getFirstSink());

        NERStringPreprocessor stringProcessor = new NERStringPreprocessor();
        stringProcessor.setSink(testPipe);
        stringProcessor.setTagSink(uriPipe);

        EndPipe out = new EndPipe();
        merge.setSink(out);

        IPipe<Relation, Relation> enchance = new RelationPrefixPostprocessor();
        out.setSink(enchance);

        return new CompressPipe<>(stringProcessor, enchance);
    }


    private class EndPipe extends APipe<Pair<Pair<RelationEntity, RelationEntity>, IClassificationTestResult>,
                                        Relation>{

        @Override
        public void push(Pair<Pair<RelationEntity, RelationEntity>, IClassificationTestResult> obj) {
            sink.push(new Relation(
                    obj.getKey().getKey(),
                    obj.getKey().getValue(),
                    obj.getValue().getPredictedRelation()
            ));
        }
    }


}
