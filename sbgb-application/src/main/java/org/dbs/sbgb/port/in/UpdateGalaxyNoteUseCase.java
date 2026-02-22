package org.dbs.sbgb.port.in;

import java.util.UUID;

public interface UpdateGalaxyNoteUseCase {
    void updateNote(UUID id, int note);
}
