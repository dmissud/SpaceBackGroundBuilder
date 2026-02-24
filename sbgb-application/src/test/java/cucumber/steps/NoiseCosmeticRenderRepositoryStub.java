package cucumber.steps;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.port.out.NoiseCosmeticRenderRepository;

import java.util.*;

public class NoiseCosmeticRenderRepositoryStub implements NoiseCosmeticRenderRepository {

    private final List<NoiseCosmeticRender> saved = new ArrayList<>();

    @Override
    public NoiseCosmeticRender save(NoiseCosmeticRender render) {
        saved.removeIf(r -> r.id().equals(render.id()));
        saved.add(render);
        return render;
    }

    @Override
    public void deleteById(UUID id) {
        saved.removeIf(r -> r.id().equals(id));
    }

    @Override
    public Optional<NoiseCosmeticRender> findById(UUID id) {
        return saved.stream().filter(r -> r.id().equals(id)).findFirst();
    }

    @Override
    public Optional<NoiseCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseStructureId, int cosmeticHash) {
        return saved.stream()
                .filter(r -> r.baseStructureId().equals(baseStructureId) && r.cosmeticHash() == cosmeticHash)
                .findFirst();
    }

    @Override
    public List<NoiseCosmeticRender> findAllByBaseStructureId(UUID baseStructureId) {
        return saved.stream().filter(r -> r.baseStructureId().equals(baseStructureId)).toList();
    }
}
