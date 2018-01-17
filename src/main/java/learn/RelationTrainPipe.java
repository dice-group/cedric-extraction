package learn;

import model.ITrainingData;
import pipeline.APipe;

import java.util.ArrayList;
import java.util.List;

public class RelationTrainPipe extends APipe<ITrainingData, IRelationClassificator>{

    public static final String STOP_SIGNAL = "relationTrainer";

    private IRelationClassificatorFactory factory;
    private List<ITrainingData> trainingData = new ArrayList<>();

    public RelationTrainPipe(IRelationClassificatorFactory factory) {
        this.factory = factory;
    }

    @Override
    public void push(ITrainingData obj) {
        trainingData.add(obj);
    }


    @Override
    public void stopSignal(String type){
        IRelationClassificator classificator = factory.trainModel(trainingData);
        trainingData.clear();
        sink.push(classificator);

        sink.stopSignal(RelationTrainPipe.STOP_SIGNAL);
    }
}
