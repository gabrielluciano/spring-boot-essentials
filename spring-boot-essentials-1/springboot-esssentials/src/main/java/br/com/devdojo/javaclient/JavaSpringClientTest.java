package br.com.devdojo.javaclient;

import br.com.devdojo.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class JavaSpringClientTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/protected/students")
                .basicAuthentication("undertaker", "senha")
                .build();

        // Single entity -> forObject
        Student student = restTemplate.getForObject("/{id}", Student.class, 8);
        System.out.println(student + "\n\n");

        // Single entity -> forEntity
        ResponseEntity<Student> entity = restTemplate.getForEntity("/{id}", Student.class, 8);
        System.out.println(entity);
        System.out.println(entity.getBody() + "\n\n");

        // Multiple entities -> forObject
        Student[] students = restTemplate.getForObject("/", Student[].class);
        System.out.println(Arrays.toString(students) + "\n\n");

        // List of entities -> exchange
        ResponseEntity<List<Student>> exchange = restTemplate.exchange("/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Student>>() {});
        System.out.println(exchange.getBody());
    }
}
