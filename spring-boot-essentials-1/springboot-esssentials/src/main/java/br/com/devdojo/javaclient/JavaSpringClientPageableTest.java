package br.com.devdojo.javaclient;

import br.com.devdojo.model.PageableResponse;
import br.com.devdojo.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JavaSpringClientPageableTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/protected/students")
                .basicAuthentication("undertaker", "senha")
                .build();

        ResponseEntity<PageableResponse<Student>> exchange = restTemplate.exchange("/?sort=name,asc&sort=email,desc", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        System.out.println(exchange);
    }
}
