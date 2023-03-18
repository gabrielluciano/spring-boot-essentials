package br.com.devdojo;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class StudentEndpointTokenTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @MockBean
    private StudentRepository studentRepository;

    @Autowired
    private MockMvc mockMvc;

    private HttpEntity<Void> protectedHeader;
    private HttpEntity<Void> adminHeader;
    private HttpEntity<Void> wrongHeader;

    @BeforeAll
    public void configProtectedHeaders() {
        // { "username": "undertaker", "password": "senha" }
        String str = "{ \"username\": \"undertaker\", \"password\": \"senha\" }";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
        this.protectedHeader = new HttpEntity<>(headers);
    }

    @BeforeAll
    public void configAdminHeaders() {
        // { "username": "john", "password": "senha" }
        String str = "{ \"username\": \"john\", \"password\": \"senha\" }";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
        this.adminHeader = new HttpEntity<>(headers);
    }

    @BeforeAll
    public void configWrongHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "11111");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Test
    public void listStudentsWhenTokenIsIncorrectShouldReturnStatusCode403() {
        ResponseEntity<String> response = restTemplate.exchange("/v1/protected/students/",
                HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void getStudentsByIdWhenStudentsTokenIsIncorrectShouldReturnStatusCode403() {
        ResponseEntity<String> response = restTemplate.exchange("/v1/protected/students/{id}",
                HttpMethod.GET, wrongHeader, String.class, 1L);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listStudentsWhenTokenIsCorrectShouldReturnStatusCode200() {
        ResponseEntity<String> response = restTemplate.exchange("/v1/protected/students/",
                HttpMethod.GET, protectedHeader, String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdStudentsWhenTokenIsCorrectShouldReturnStatusCode200() {
        Optional<Student> studentOptional = Optional.of(new Student(1L, "Bruno", "bruno@email.com"));
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(studentOptional);
        ResponseEntity<Student> response = restTemplate.exchange("/v1/protected/students/{id}",
                HttpMethod.GET, protectedHeader, Student.class, 1L);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(response.getBody().getId()).isEqualTo(studentOptional.get().getId());
    }

    @Test
    public void getStudentsByIdStudentsWhenTokenIsCorrectAndStudentDoesNotExistShouldReturnStatusCode404() {
        long id = 1L;
        BDDMockito.when(studentRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Student> response = restTemplate.exchange("/v1/protected/students/{id}",
                HttpMethod.GET, protectedHeader, Student.class, id);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode204() {
        Optional<Student> studentOptional = Optional.of(new Student(1L, "Bruno", "bruno@email.com"));
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(studentOptional);
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE,
                adminHeader, String.class, 1L);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentNotExistsShouldReturnStatusCode404() throws Exception {
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/admin/students/{id}", 1L)
                        .headers(adminHeader.getHeaders()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteWhenUserDoesNotHaveRoleAdminShouldReturnStatusCode403() throws Exception {
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/admin/students/{id}", 1L)
                        .headers(protectedHeader.getHeaders()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400() {
        Student student = new Student(1L, null, "bruno@email.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<String> response = restTemplate.exchange("/v1/admin/students",
                HttpMethod.POST, new HttpEntity<>(student, adminHeader.getHeaders()), String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        Assertions.assertThat(response.getBody()).contains("fieldMessage","O campo nome é obrigatório");
    }

    @Test
    public void createShouldPersistData() {
        Student student = new Student(1L, "Bruno", "bruno@email.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<Student> response = restTemplate.exchange("/v1/admin/students",
                HttpMethod.POST, new HttpEntity<>(student, adminHeader.getHeaders()), Student.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(201);
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Bruno");
    }
}
