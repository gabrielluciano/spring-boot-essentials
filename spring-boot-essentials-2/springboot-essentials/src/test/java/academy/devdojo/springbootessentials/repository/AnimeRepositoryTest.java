package academy.devdojo.springbootessentials.repository;

import academy.devdojo.springbootessentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Anime Repository")
@Log4j2
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save persist Anime when successful")
    void save_PersistAnime_WhenSuccessful() {
        Anime anime = animeRepository.save(createAnime());

        assertThat(anime).isNotNull();

        assertThat(anime.getId()).isNotNull();

        assertThat(anime.getName()).isEqualTo("Some Anime");
    }

    @Test
    @DisplayName("Save updates Anime when successful")
    void save_UpdatesAnime_WhenSuccessful() {
        Anime anime = animeRepository.save(createAnime());
        anime.setName("Overlord");
        Anime updatedAnime = animeRepository.save(anime);

        log.info(updatedAnime);

        assertThat(updatedAnime).isNotNull();

        assertThat(updatedAnime.getId()).isNotNull();

        assertThat(updatedAnime.getName()).isEqualTo("Overlord");
    }

    @Test
    @DisplayName("Save removes Anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        Anime anime = animeRepository.save(createAnime());
        animeRepository.delete(anime);
        Optional<Anime> animeOptional = animeRepository.findById(anime.getId());

        assertThat(animeOptional).isEmpty();
    }

    @Test
    @DisplayName("Find By Name returns list of Anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime anime = animeRepository.save(createAnime());
        List<Anime> animes = animeRepository.findByName(anime.getName());

        assertThat(animes)
                .isNotEmpty()
                .hasSize(1)
                .contains(anime);
    }

    @Test
    @DisplayName("Find By Name returns empty list when no anime is found")
    void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        Anime anime = animeRepository.save(createAnime());
        List<Anime> animes = animeRepository.findByName("this anime does not exist");

        assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Save throws ConstraintViolationException when name is empty")
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        Anime anime = new Anime();

       assertThatExceptionOfType(ConstraintViolationException.class)
               .isThrownBy(() -> animeRepository.save(anime))
               .withMessageContaining("The anime name cannot be blank");
    }

    private Anime createAnime() {
        return Anime.builder()
                .name("Some Anime")
                .build();
    }

}
