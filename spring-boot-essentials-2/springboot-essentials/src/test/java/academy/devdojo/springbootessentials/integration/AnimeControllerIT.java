package academy.devdojo.springbootessentials.integration;

import academy.devdojo.springbootessentials.domain.Anime;
import academy.devdojo.springbootessentials.repository.AnimeRepository;
import academy.devdojo.springbootessentials.util.AnimeCreator;
import academy.devdojo.springbootessentials.util.AnimePostRequestBodyCreator;
import academy.devdojo.springbootessentials.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Spring version below 2.5
class AnimeControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AnimeRepository animeRepository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("list returns list of animes inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

        assertThat(animePage).isNotNull();

        assertThat(animePage.getContent())
                .isNotEmpty()
                .hasSize(1);

        assertThat(animePage.getContent().get(0).getName()).isEqualTo(savedAnime.getName());
    }

    @Test
    @DisplayName("listAll returns list of animes when successful")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(savedAnime.getName());
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime expectedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        Anime anime = testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedAnime.getId());

        assertThat(anime)
                .isNotNull()
                .isEqualTo(expectedAnime);

        assertThat(anime.getId()).isEqualTo(expectedAnime.getId());
    }

    @Test
    @DisplayName("findByName returns a list of animes when successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        Anime expectedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        String url = String.format("/animes/find?name=%s", expectedAnime.getName());
        List<Anime> animes = testRestTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes)
                .isNotNull()
                .isInstanceOf(List.class)
                .isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedAnime.getName());
    }

    @Test
    @DisplayName("findByName returns an empty list of animes when successful")
    void findByName_ReturnsEmptyListOfAnimes_WhenAnimeIsNotFound() {
        String url = String.format("/animes/find?name=%s", "anime");

        List<Anime> animes = testRestTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        ResponseEntity<Anime> animeResponseEntity = testRestTemplate.postForEntity("/animes",
                AnimePostRequestBodyCreator.createValidAnimePostRequestBody(),
                Anime.class);

        assertThat(animeResponseEntity).isNotNull();

        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(animeResponseEntity.getBody()).isNotNull();

        assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_ReturnsUpdatesAnimeStatus204NoContent_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("New name");

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange("/animes", HttpMethod.PUT,
                new HttpEntity<>(savedAnime), Void.class);

        assertThat(responseEntity).isNotNull();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange("/animes/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());

        assertThat(responseEntity).isNotNull();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(responseEntity.getBody()).isNull();
    }
}
