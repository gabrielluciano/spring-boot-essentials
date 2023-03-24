package academy.devdojo.springbootessentials.client;

import academy.devdojo.springbootessentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {

    public static void main(String[] args) {

        // GET - getForEntity - retorna ResponseEntity
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/4", Anime.class);
        log.info(entity);

        // GET - getForObject - retorna Entidade
        Anime anime = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 4);
        log.info(anime);

        // GET - getForObject com array - retorna um array da Entidade
        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(animes));

        // GET - exchange com lista - retorna uma List da Entidade
        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        log.info(exchange.getBody());

        // POST - postForObject
        Anime kingdom = Anime.builder().name("kingdom").build();
        Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
        log.info("Saved anime: {}", kingdomSaved);

        // POST - postForObject
        Anime samurai = Anime.builder().name("Samurai Champloo").build();
        ResponseEntity<Anime> samuraiExchange = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.POST, new HttpEntity<>(samurai, createJsonHeader()), new ParameterizedTypeReference<>() {});
        log.info("Saved anime: {}", samuraiExchange.getBody());
    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
