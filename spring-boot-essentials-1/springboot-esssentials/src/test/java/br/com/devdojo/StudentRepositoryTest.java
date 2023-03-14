package br.com.devdojo;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void createShouldPersistData() {
        Student student = new Student("João", "joao@email.com");
        this.studentRepository.save(student);
        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("João");
        assertThat(student.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    public void deleteShouldRemoveData() {
        Student student = new Student("João", "joao@email.com");
        this.studentRepository.save(student);
        studentRepository.delete(student);
        assertThat(studentRepository.findById(student.getId()).isEmpty()).isTrue();
    }

    @Test
    public void updateShouldChangeAndPersistData() {
        Student student = new Student("João", "joao@email.com");
        this.studentRepository.save(student);
        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("João");
        assertThat(student.getEmail()).isEqualTo("joao@email.com");

        student.setName("João Silva");
        this.studentRepository.save(student);
        student = this.studentRepository.findById(student.getId()).get();
        assertThat(student.getName()).isEqualTo("João Silva");
    }

    @Test
    public void findByNameIgnoreCaseContainingShouldReturnTwoStudents() {
        Student student1 = new Student("João", "joao@email.com");
        Student student2 = new Student("José", "mario@email.com");
        this.studentRepository.save(student1);
        this.studentRepository.save(student2);
        List<Student> students = this.studentRepository
                .findByNameIgnoreCaseContaining("jo");
        assertThat(students.size()).isEqualTo(2);
    }

    @Test
    public void createWhenNameIsNullShouldThrowConstraintViolationException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            Student student = new Student();
            studentRepository.save(student);
        });
    }
}
