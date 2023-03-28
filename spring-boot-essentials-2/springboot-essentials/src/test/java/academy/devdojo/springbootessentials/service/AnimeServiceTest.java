package academy.devdojo.springbootessentials.service;

import academy.devdojo.springbootessentials.domain.Anime;
import academy.devdojo.springbootessentials.exception.BadRequestException;
import academy.devdojo.springbootessentials.repository.AnimeRepository;
import academy.devdojo.springbootessentials.util.AnimeCreator;
import academy.devdojo.springbootessentials.util.AnimePostRequestBodyCreator;
import academy.devdojo.springbootessentials.util.AnimePutRequestBodyCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        List<Anime> animeList = List.of(AnimeCreator.createValidAnime());

        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(animeList);

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(animeList);

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("listPaged returns list of animes inside page object when successful")
    void listPaged_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listPaged(PageRequest.of(0, 2));

        assertThat(animePage).isNotNull();

        assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of animes when successful")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.listAll();

        assertThat(animeList)
                .isNotNull()
                .isInstanceOf(List.class)
                .isNotEmpty()
                .hasSize(1);

        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when successful")
    void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
        Anime expectedAnime = AnimeCreator.createValidAnime();
        Anime anime = animeService.findByIdOrThrowBadRequestException(1L);

        assertThat(anime)
                .isNotNull()
                .isEqualTo(expectedAnime);

        assertThat(anime.getId()).isEqualTo(expectedAnime.getId());
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadRequestException when anime is not found")
    void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class).isThrownBy(() ->
                animeService.findByIdOrThrowBadRequestException(1L));
    }

    @Test
    @DisplayName("findByName returns a list of animes when successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.findByName("Some Anime");

        assertThat(animeList)
                .isNotNull()
                .isInstanceOf(List.class)
                .isNotEmpty()
                .hasSize(1);

        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of animes when successful")
    void findByName_ReturnsEmptyListOfAnimes_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeService.findByName("Some Anime");

        assertThat(animeList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime expectedAnime = AnimeCreator.createValidAnime();

        Anime anime = animeService.save(AnimePostRequestBodyCreator.createValidAnimePostRequestBody());

        assertThat(anime)
                .isNotNull()
                .isEqualTo(expectedAnime);
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_ReturnsUpdatesAnimeStatus204NoContent_WhenSuccessful() {

        assertThatNoException().isThrownBy(() -> animeService.replace(AnimePutRequestBodyCreator
                .createValidAnimePutRequestBody()));
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {

        assertThatNoException().isThrownBy(() -> animeService.delete(1));
    }

}
