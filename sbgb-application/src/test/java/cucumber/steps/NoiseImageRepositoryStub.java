package cucumber.steps;

import lombok.Getter;
import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.port.out.NoiseImageRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class NoiseImageRepositoryStub implements NoiseImageRepository {
    private NoiseImage savedImage;

    @Override
    public NoiseImage save(NoiseImage noiseImage) {
        this.savedImage = noiseImage;
        return noiseImage;
    }

    @Override
    public List<NoiseImage> findAll() {
        return (savedImage != null) ? List.of(savedImage) : Collections.emptyList();
    }

    @Override
    public java.util.Optional<NoiseImage> findByName(String name) {
        return (savedImage != null && name.equals(savedImage.getName())) ? java.util.Optional.of(savedImage) : java.util.Optional.empty();
    }

    @Override
    public void updateNote(UUID id, int note) {
        if (savedImage != null && savedImage.getId().equals(id)) {
            savedImage.setNote(note);
        }
    }
}
