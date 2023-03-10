package br.com.devdojo.javaclient;

import br.com.devdojo.model.Student;

public class SpringClientTest {

    public static void main(String[] args) {

        Student student = new Student();
        student.setName("Mário Souza");
        student.setEmail("mariosza@mail.com");
        student.setId(17L);

        JavaClientDAO dao = new JavaClientDAO();

//        System.out.println(dao.findById(12L));
//        System.out.println(dao.listAll());
//        System.out.println(dao.save(student));
//        dao.update(student);
        dao.delete(80L);
    }
}
