package ch.unil.doplab.studybuddy.business;

public class Student extends User {
    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String email, String username) {
        super(firstName, lastName, email, username);
    }

    public void merge(Student student) {
        super.merge(student);
    }
    public String describe() {
        return super.describe();
    }
}
