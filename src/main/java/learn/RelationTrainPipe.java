package learn;

import model.ITrainingData;
import pipeline.APipe;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * A pipe which allows to train a given relation classifier.
 * The pipe can be reused after a stop signal.
 *
 * @author Cedric Richter
 */
public class RelationTrainPipe extends APipe<ITrainingData, IRelationClassifier>{

    public static final String STOP_SIGNAL = "relationTrainer";

    private IRelationClassifierFactory factory;
    private List<ITrainingData> trainingData = new ArrayList<>();

    public RelationTrainPipe(IRelationClassifierFactory factory) {
        this.factory = factory;
    }

    @Override
    public void push(ITrainingData obj) {
        trainingData.add(obj);
    }


    @Override
    public void stopSignal(String type){
        IRelationClassifier classificator = factory.trainModel(trainingData);
        trainingData.clear();
        sink.push(classificator);

        sink.stopSignal(RelationTrainPipe.STOP_SIGNAL);
    }
}
