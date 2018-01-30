

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
import preprocessing.string.PrintOutSink;
import preprocessing.string.URIFinderPipe;

public class RelationExtPipeFactory {

    public IPipe<IDBLoadingTask, ILabelledEntity> createDBLoadingPipe(IEntityMapper mapper){
        return new MongoLoadingPipe(mapper);
    }

    public IPipe<ILabelledEntity, IRelationClassificator> createTrainingPipe(int windowWidth,
                                                                   IBayesEstimator untrainedEstimator,
                                                                   IRelationClassificatorFactory factory){

        IPipe<ITrainingData, IRelationClassificator> trainModelPipe = new RelationTrainPipe(
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

    public IPipe<ILabelledEntity, IClassificationTestResult> createTestingPipe(IRelationClassificator classificator,
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

    public IPipe<String, Relation> createRelationExtractionPipe(IRelationClassificator classificator, int windowWidth){

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
