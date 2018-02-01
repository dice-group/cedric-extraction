package io;

import model.ILabelledEntity;
import org.bson.BSONObject;

/**
 *
 * A mapper which can map MongoDB entries to labelled data.
 *
 * @author Cedric Richter
 */
public interface IEntityMapper {

    public ILabelledEntity mapEntity(BSONObject object);

}
