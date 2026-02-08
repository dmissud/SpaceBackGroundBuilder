package cucumber.steps;

import lombok.Getter;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.port.out.NoiseImageRepository;

import java.util.Collections;
import java.util.List;

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
}
