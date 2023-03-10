package br.com.devdojo.javaclient;

import br.com.devdojo.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JavaSpringClientPostTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/admin/students")
                .basicAuthentication("john", "senha").build();

        Student student = new Student();
        student.setName("Andy");
        student.setEmail("andy1@mail.com");

        // exchange
        ResponseEntity<Student> exchange = restTemplate.exchange("/", HttpMethod.POST,
                new HttpEntity<>(student, createJSONHeader()), Student.class);
        System.out.println(exchange);

        // forObject
        Student studentPost = restTemplate.postForObject("/", student, Student.class);
        System.out.println(studentPost);

        // forEntity
        ResponseEntity<Student> studentEntity = restTemplate.postForEntity("/", student, Student.class);
        System.out.println(studentEntity);
    }


    private static HttpHeaders createJSONHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
