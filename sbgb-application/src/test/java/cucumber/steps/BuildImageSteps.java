package cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.domain.service.ImagesService;
import org.dbs.sbgb.port.in.BuildNoiseImageUseCase;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseBaseStructureRepository;
import org.dbs.sbgb.port.out.NoiseCosmeticRenderRepository;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildImageSteps {

    private final NoiseBaseStructureRepositoryStub baseRepo = new NoiseBaseStructureRepositoryStub();
    private final NoiseCosmeticRenderRepositoryStub renderRepo = new NoiseCosmeticRenderRepositoryStub();
    private final ImagesService imagesService = new ImagesService(baseRepo, renderRepo);

    private byte[] builtImage;
    private NoiseCosmeticRender savedRender;

    @When("je construis une image de bruit avec les caractéristiques suivantes :")
    public void je_construis_une_image_de_bruit(Map<String, String> data) throws IOException {
        ImageRequestCmd cmd = buildCmd(data, 0);
        builtImage = imagesService.buildNoiseImage(cmd);
    }

    @When("je note un rendu cosmétique avec les caractéristiques suivantes :")
    public void je_note_un_rendu_cosmetique(Map<String, String> data) throws IOException {
        int note = Integer.parseInt(data.get("note"));
        ImageRequestCmd cmd = buildCmd(data, note);
        savedRender = imagesService.rate(cmd);
    }

    @Then("l'image est générée avec succès")
    public void l_image_est_generee_avec_succes() {
        assertThat(builtImage).isNotNull().isNotEmpty();
    }

    @Then("la base de structure est sauvegardée")
    public void la_base_est_sauvegardee() {
        assertThat(baseRepo.getSavedBase()).isNotNull();
    }

    @Then("le rendu cosmétique est sauvegardé avec la note {int}")
    public void le_rendu_est_sauvegarde_avec_la_note(int note) {
        assertThat(savedRender).isNotNull();
        assertThat(savedRender.note()).isEqualTo(note);
    }

    private ImageRequestCmd buildCmd(Map<String, String> data, int note) {
        String[] dimensions = data.get("taille").split("x");
        int width = Integer.parseInt(dimensions[0].trim());
        int height = Integer.parseInt(dimensions[1].trim());

        String[] backData = data.get("back").split(" ");
        String[] middleData = data.get("middle").split(" ");

        return ImageRequestCmd.builder()
                .note(note)
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(width).height(height)
                        .seed(Integer.parseInt(data.get("seed")))
                        .build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back(backData[0])
                        .backThreshold(Double.parseDouble(backData[1]))
                        .middle(middleData[0])
                        .middleThreshold(Double.parseDouble(middleData[1]))
                        .fore(data.get("front").trim())
                        .build())
                .build();
    }
}
