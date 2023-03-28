package academy.devdojo.springbootessentials.util;

import academy.devdojo.springbootessentials.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("Some Anime")
                .build();
    }

    public static Anime createValidAnime() {
        return Anime.builder()
                .name("Some Anime")
                .id(1L)
                .build();
    }

    public static Anime createValidUpdatedAnime() {
        return Anime.builder()
                .name("Updated Anime")
                .id(1L)
                .build();
    }

}
