package academy.devdojo.springbootessentials.controller;

import academy.devdojo.springbootessentials.domain.Anime;
import academy.devdojo.springbootessentials.requests.AnimePostRequestBody;
import academy.devdojo.springbootessentials.requests.AnimePutRequestBody;
import academy.devdojo.springbootessentials.service.AnimeService;
import academy.devdojo.springbootessentials.util.AnimeCreator;
import academy.devdojo.springbootessentials.util.AnimePostRequestBodyCreator;
import academy.devdojo.springbootessentials.util.AnimePutRequestBodyCreator;
import academy.devdojo.springbootessentials.util.DateUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeServiceMock;

    @Mock
    private DateUtil dateUtil;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(animeController, "dateUtil", dateUtil);

        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        List<Anime> animeList = List.of(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.listPaged(ArgumentMatchers.any())).thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAll())
                .thenReturn(animeList);

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(animeList);

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("list returns list of animes inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();

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
        List<Anime> animeList = animeController.listAll().getBody();

        assertThat(animeList)
                .isNotNull()
                .isInstanceOf(List.class)
                .isNotEmpty()
                .hasSize(1);

        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime expectedAnime = AnimeCreator.createValidAnime();
        Anime anime = animeController.findById(1L).getBody();

        assertThat(anime)
                .isNotNull()
                .isEqualTo(expectedAnime);

        assertThat(anime.getId()).isEqualTo(expectedAnime.getId());
    }

    @Test
    @DisplayName("findByName returns a list of animes when successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeController.findByName("Some Anime").getBody();

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
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeController.findByName("Some Anime").getBody();

        assertThat(animeList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime expectedAnime = AnimeCreator.createValidAnime();

        ResponseEntity<Anime> animeResponseEntity = animeController.save(AnimePostRequestBodyCreator
                .createValidAnimePostRequestBody());

        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(animeResponseEntity.getBody()).isEqualTo(expectedAnime);
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_ReturnsUpdatesAnimeStatus204NoContent_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = animeController
                .replace(AnimePutRequestBodyCreator.createValidAnimePutRequestBody());

        assertThatNoException().isThrownBy(() -> animeController.replace(AnimePutRequestBodyCreator
                .createValidAnimePutRequestBody()));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = animeController.delete(1);

        assertThatNoException().isThrownBy(() -> animeController.delete(1));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(responseEntity.getBody()).isNull();
    }

}
