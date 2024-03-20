package ch.unil.doplab.studybuddy.business;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class ApplicationState {

    private Map<UUID,Student> students;

    @PostConstruct
    public void init() {
        students = new TreeMap<>();
        populateStudents();
    }
    public Student addStudent(Student student) {
        return addStudent(UUID.randomUUID(), student);
    }

    public Student addStudent(UUID id, Student student) {
        student.setId(id);
        students.put(id, student);
        return student;
    }

    public boolean setStudent(UUID id, Student student) {
        var theStudent = students.get(id);
        if (theStudent == null) {
            return false;
        }
        theStudent.merge(student);
        return true;
        }

    public boolean removeStudent(UUID id) {
        return students.remove(id) != null;
    }

    public Student getStudent(UUID uuid) {
        return students.get(uuid);
    }

    public Map<UUID,Student> getAllStudents() {
        return students;
    }

    private void populateStudents() {
        addStudent(UUID.fromString("b8d0c81d-e1c6-4708-bd02-d218a23e4805"), new Student("paul", "Smith", "paul.smith@gmail.com", "paul"));
        addStudent(UUID.fromString("0ab2ec68-c574-4d81-bed0-a93c31fab1c0"), new Student("Jane", "Doe", "jane.doe@icloud.com", "jane"));
        addStudent(UUID.fromString("5d53a98b-53a8-4580-adc1-28067b37582a"), new Student("Jean", "Dupont", "jean.dupont@facebook.com", "jean"));
    }
}
