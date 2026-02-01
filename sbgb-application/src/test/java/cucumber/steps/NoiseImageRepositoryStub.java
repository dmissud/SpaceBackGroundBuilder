package cucumber.steps;

import lombok.Getter;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.port.out.NoiseImageRepository;

@Getter
public class NoiseImageRepositoryStub implements NoiseImageRepository {
    private NoiseImage savedImage;

    @Override
    public NoiseImage save(NoiseImage noiseImage) {
        this.savedImage = noiseImage;
        return noiseImage;
    }

}
