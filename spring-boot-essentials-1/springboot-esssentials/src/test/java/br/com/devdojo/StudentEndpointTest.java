package br.com.devdojo;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
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
@AutoConfigureMockMvc
public class StudentEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @MockBean
    private StudentRepository studentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void listStudentsWhenUsernameAndPasswordAreIncorrectShouldReturnStatusCode401() {
        System.out.println(port);
        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/", String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    public void getStudentsByIdStudentsWhenUsernameAndPasswordAreIncorrectShouldReturnStatusCode401() {
        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/1", String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    public void listStudentsWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200() {
        List<Student> students = Arrays.asList(
                new Student(1L, "Bruno", "bruno@email.com"),
                new Student(2L, "Larissa", "larissa@email.com")
        );
        BDDMockito.when(studentRepository.findAll()).thenReturn(students);
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/", String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdStudentsWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200() {
        Optional<Student> studentOptional = Optional.of(new Student(1L, "Bruno", "bruno@email.com"));
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(studentOptional);
        ResponseEntity<Student> response = restTemplate.getForEntity("/v1/protected/students/1", Student.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(response.getBody().getId()).isEqualTo(studentOptional.get().getId());
    }

    @Test
    public void getStudentsByIdStudentsWhenUsernameAndPasswordAreCorrectAndStudentDoesNotExistShouldReturnStatusCode404() {
        long id = 1L;
        BDDMockito.when(studentRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Student> response = restTemplate.getForEntity("/v1/protected/students/{id}", Student.class, id);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode204() {
        Optional<Student> studentOptional = Optional.of(new Student(1L, "Bruno", "bruno@email.com"));
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(studentOptional);
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE,
                null, String.class, 1L);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    @WithMockUser(username = "xx", password = "xx", roles = "ADMIN")
    public void deleteWhenUserHasRoleAdminAndStudentNotExistsShouldReturnStatusCode404() throws Exception {
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
//        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE,
//                null, String.class, 1L);
//        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/admin/students/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "xx", password = "xx", roles = "USER")
    public void deleteWhenUserDoesNotHaveRoleAdminShouldReturnStatusCode403() throws Exception {
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/admin/students/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400() {
        Student student = new Student(1L, null, "bruno@email.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/admin/students", student, String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        Assertions.assertThat(response.getBody()).contains("fieldMessage","O campo nome é obrigatório");
    }

    @Test
    public void createShouldPersistData() {
        Student student = new Student(1L, "Bruno", "bruno@email.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<Student> response = restTemplate.postForEntity("/v1/admin/students", student, Student.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(201);
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Bruno");
    }

    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().basicAuthentication("john", "senha");
        }
    }
}
