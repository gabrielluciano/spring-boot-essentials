package academy.devdojo.springbootessentials.service;

import academy.devdojo.springbootessentials.domain.Anime;
import academy.devdojo.springbootessentials.exception.BadRequestException;
import academy.devdojo.springbootessentials.mapper.AnimeMapper;
import academy.devdojo.springbootessentials.repository.AnimeRepository;
import academy.devdojo.springbootessentials.requests.AnimePostRequestBody;
import academy.devdojo.springbootessentials.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public List<Anime> listAll() {
        return animeRepository.findAll();
    }

    public Anime findByIdOrThrowBadRequestException(long id) {
        return animeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime not Found"));
    }

    public List<Anime> findByName(String name) {
        return animeRepository.findByName(name);
    }

    @Transactional(rollbackOn = Exception.class)
    public Anime save(AnimePostRequestBody animePostRequestBody) {
        Anime anime = AnimeMapper.INSTANCE.toAnime(animePostRequestBody);
        anime = animeRepository.save(anime);
        if (true)
            throw new RuntimeException("bad code");
        return anime;
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime animeFromDb = findByIdOrThrowBadRequestException(animePutRequestBody.getId());
        Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
        anime.setId(animeFromDb.getId());
        animeRepository.save(anime);
    }

    public void delete(long id) {
        animeRepository.delete(findByIdOrThrowBadRequestException(id));
    }
}
