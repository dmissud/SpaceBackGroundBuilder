package org.dbs.sbgb.port.in;

import java.util.UUID;

public interface UpdateNoiseImageNoteUseCase {
    void updateNote(UUID id, int note);
}
