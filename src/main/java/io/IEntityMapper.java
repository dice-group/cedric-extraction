package io;

import model.ILabelledEntity;
import org.bson.BSONObject;

public interface IEntityMapper {

    public ILabelledEntity mapEntity(BSONObject object);

}
