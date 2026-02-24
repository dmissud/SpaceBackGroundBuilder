package cucumber.steps;

import lombok.Getter;
import org.dbs.sbgb.domain.model.NoiseBaseStructure;
import org.dbs.sbgb.port.out.NoiseBaseStructureRepository;

import java.util.*;

@Getter
public class NoiseBaseStructureRepositoryStub implements NoiseBaseStructureRepository {

    private final List<NoiseBaseStructure> saved = new ArrayList<>();

    public NoiseBaseStructure getSavedBase() {
        return saved.isEmpty() ? null : saved.get(0);
    }

    @Override
    public NoiseBaseStructure save(NoiseBaseStructure structure) {
        saved.removeIf(b -> b.id().equals(structure.id()));
        saved.add(structure);
        return structure;
    }

    @Override
    public List<NoiseBaseStructure> findAll() {
        return List.copyOf(saved);
    }

    @Override
    public Optional<NoiseBaseStructure> findByConfigHash(int hash) {
        return saved.stream().filter(b -> b.configHash() == hash).findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        saved.removeIf(b -> b.id().equals(id));
    }

    @Override
    public NoiseBaseStructure updateMaxNote(UUID id, int maxNote) {
        NoiseBaseStructure existing = saved.stream().filter(b -> b.id().equals(id)).findFirst().orElseThrow();
        NoiseBaseStructure updated = new NoiseBaseStructure(existing.id(), existing.description(), maxNote,
                existing.width(), existing.height(), existing.seed(), existing.octaves(),
                existing.persistence(), existing.lacunarity(), existing.scale(),
                existing.noiseType(), existing.useMultiLayer(), existing.layersConfig());
        saved.removeIf(b -> b.id().equals(id));
        saved.add(updated);
        return updated;
    }
}
