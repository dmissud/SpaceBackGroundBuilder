package cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.domain.service.ImagesService;
import org.dbs.spgb.port.in.CreateNoiseImageUseCase;
import org.dbs.spgb.port.in.ImageRequestCmd;
import org.dbs.spgb.port.out.NoiseImageRepository;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildImageSteps {
    private final CreateNoiseImageUseCase createNoiseImageUseCase;
    private ImageRequestCmd.SizeCmd sizeCmd;
    private ImageRequestCmd.ColorCmd.ColorCmdBuilder colorCmdBuilder;
    private NoiseImage noiseImage;

    public BuildImageSteps() {
        NoiseImageRepository noiseImageRepository = imageData -> imageData;
        this.createNoiseImageUseCase = new ImagesService(noiseImageRepository);
    }


    @Given("Il n y a pas d image de fond d ecran")
    public void il_n_y_a_pas_d_image_de_fond_d_écran() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @When("je cree une image de fond d'ecran avec la taille '{int}' x '{int}' et un seed '{int}'")
    public void je_cree_une_image_de_fond_d_ecran_avec_la_taille_x_et_un_seed(Integer with, Integer heigth, Integer seed) {
        sizeCmd = ImageRequestCmd.SizeCmd.builder()
                .height(heigth)
                .width(with)
                .seed(seed)
                .build();
    }

    @When("les couleurs sont en back {string} et en front {string} et pour middle {string}")
    public void les_couleurs_sont_en_back_et_en_front_et_pour_middle(String back, String fore, String middle) {
        colorCmdBuilder = ImageRequestCmd.ColorCmd.builder()
                .back(back)
                .fore(fore)
                .middle(middle);
    }

    @When("les valeurs de changement sont de {string} pour le back et {string}  pour le middle")
    public void les_valeurs_de_changement_sont_de_pour_le_back_et_pour_le_middle(String backThreshold, String middleThreshold) {
        ImageRequestCmd imageRequestCmd = ImageRequestCmd.builder()
                .sizeCmd(sizeCmd)
                .colorCmd(colorCmdBuilder
                        .backThreshold(Double.parseDouble(backThreshold))
                        .middleThreshold(Double.parseDouble(middleThreshold))
                        .build())
                .build();
        try {
            noiseImage = createNoiseImageUseCase.createNoiseImage(imageRequestCmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("je veux que l'image soit creee avec le nom {string} et le type {string}")
    public void je_veux_que_l_image_soit_creee_avec_le_nom_et_le_type(String string, String string2) {
        assertThat(noiseImage).isNotNull();
        assertThat(noiseImage.getId()).isNotNull();
        assertThat(noiseImage.getImage()).isNotEmpty();
    }

}
