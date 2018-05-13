package org.dice_research.cedric_extraction.preprocessing.string;

import javafx.util.Pair;
import org.dice_research.cedric_extraction.model.NEREntity;
import org.dice_research.cedric_extraction.preprocessing.string.*;
import org.junit.jupiter.api.Test;

class URIFinderPipeTest {

    @Test
    void addURI() {

        NEREntity entity1 = new NEREntity("Abraham Lincoln marries Michelle Obama. She hate it.",
                "Abraham Lincoln", "PERSON");

        NEREntity entity2 = new NEREntity("Abraham Lincoln marries Michelle Obama.",
                "Michelle Obama", "PERSON");

        Pair<NEREntity, NEREntity> pair = new Pair<>(entity1, entity2);

        new URIFinderPipe().addURI(pair);

    }
}