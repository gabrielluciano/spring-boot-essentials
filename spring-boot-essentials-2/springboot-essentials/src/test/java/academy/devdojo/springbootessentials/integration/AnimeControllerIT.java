package academy.devdojo.springbootessentials.integration;

import academy.devdojo.springbootessentials.domain.Anime;
import academy.devdojo.springbootessentials.domain.DevDojoUser;
import academy.devdojo.springbootessentials.repository.AnimeRepository;
import academy.devdojo.springbootessentials.repository.DevDojoUserRepository;
import academy.devdojo.springbootessentials.util.AnimeCreator;
import academy.devdojo.springbootessentials.util.AnimePostRequestBodyCreator;
import academy.devdojo.springbootessentials.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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

    private static final DevDojoUser USER = DevDojoUser.builder()
            .name("Dev Dojo")
            .username("devdojo")
            .password("{bcrypt}$2a$10$CDtd6F/m5xM71VdGTESYQO9YX4NFcTvWhWg8K9dwfV4r/LL86OgQW")
            .authorities("ROLE_USER")
            .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
            .name("Dev Dojo")
            .username("gabriel")
            .password("{bcrypt}$2a$10$CDtd6F/m5xM71VdGTESYQO9YX4NFcTvWhWg8K9dwfV4r/LL86OgQW")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private DevDojoUserRepository devDojoUserRepository;

    @Test
    @DisplayName("list returns list of animes inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        assertThat(animePage).isNotNull();

        assertThat(animePage.getContent())
                .isNotEmpty()
                .hasSize(1);

        assertThat(animePage.getContent().get(0).getName()).isEqualTo(savedAnime.getName());
    }

    @Test
    @DisplayName("listAll returns list of animes when successful")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(savedAnime.getName());
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);

        Anime expectedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedAnime.getId());

        assertThat(anime)
                .isNotNull()
                .isEqualTo(expectedAnime);

        assertThat(anime.getId()).isEqualTo(expectedAnime.getId());
    }

    @Test
    @DisplayName("findByName returns a list of animes when successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        devDojoUserRepository.save(USER);

        Anime expectedAnime = animeRepository.save(AnimeCreator.createValidAnime());

        String url = String.format("/animes/find?name=%s", expectedAnime.getName());
        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

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
        devDojoUserRepository.save(USER);

        String url = String.format("/animes/find?name=%s", "anime");

        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        devDojoUserRepository.save(ADMIN);

        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleAdmin.postForEntity("/animes",
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
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("New name");

        ResponseEntity<Void> responseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT,
                new HttpEntity<>(savedAnime), Void.class);

        assertThat(responseEntity).isNotNull();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        devDojoUserRepository.save(ADMIN);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> responseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());

        assertThat(responseEntity).isNotNull();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> responseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());

        assertThat(responseEntity).isNotNull();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(responseEntity.getBody()).isNull();
    }

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("devdojo", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("gabriel", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }
}
