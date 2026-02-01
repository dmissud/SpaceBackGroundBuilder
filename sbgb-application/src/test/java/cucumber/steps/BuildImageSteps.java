package cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.domain.service.ImagesService;
import org.dbs.spgb.port.in.CreateNoiseImageUseCase;
import org.dbs.spgb.port.in.ImageRequestCmd;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildImageSteps {
    private final CreateNoiseImageUseCase createNoiseImageUseCase;
    private final NoiseImageRepositoryStub noiseImageRepository;
    private NoiseImage noiseImage;

    public BuildImageSteps() {
        this.noiseImageRepository = new NoiseImageRepositoryStub();
        this.createNoiseImageUseCase = new ImagesService(noiseImageRepository);
    }


    @When("je crée une image de fond d'écran avec les caractéristiques suivantes :")
    public void je_crée_une_image_de_fond_d_écran_avec_les_caractéristiques_suivantes(Map<String, String> data) throws IOException {
        String[] dimensions = data.get("taille").split("x");
        int width = Integer.parseInt(dimensions[0].trim());
        int height = Integer.parseInt(dimensions[1].trim());

        String[] backColorData = data.get("back").split(" ");
        String[] middleColorData = data.get("middle").split(" ");

        ImageRequestCmd imageRequestCmd = ImageRequestCmd.builder()
                .name(data.get("nom"))
                .type(data.get("type"))
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(width)
                        .height(height)
                        .seed(Integer.parseInt(data.get("seed")))
                        .build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back(backColorData[0])
                        .backThreshold(Double.parseDouble(backColorData[1]))
                        .middle(middleColorData[0])
                        .middleThreshold(Double.parseDouble(middleColorData[1]))
                        .fore(data.get("front").trim())
                        .build())
                .build();

        noiseImage = createNoiseImageUseCase.createNoiseImage(imageRequestCmd);
    }

    @Then("l'image est générée avec succès")
    public void l_image_est_générée_avec_succès() {
        assertThat(noiseImage).isNotNull();
        assertThat(noiseImage.getId()).isNotNull();
        assertThat(noiseImage.getImage()).isNotEmpty();
    }

    @Then("elle est sauvegardée avec la description {string}")
    public void elle_est_sauvegardée_avec_la_description(String expectedDescription) {
        NoiseImage savedNoiseImage = noiseImageRepository.getSavedImage();

        assertThat(savedNoiseImage).isNotNull();
        assertThat(savedNoiseImage.getDescription()).isEqualTo(expectedDescription);
    }

}
