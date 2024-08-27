package ch.unil.doplab.studybuddy.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@ApplicationScoped
public class ApplicationState {

    private Map<UUID, Student> students;
    private Map<UUID, Teacher> teachers;
    private Set<String> topics;

    @PostConstruct
    public void init() {
        students = new TreeMap<>();
        teachers = new TreeMap<>();
        topics = new TreeSet<>();
        populateApplicationState();
    }

    public Student addStudent(Student student) {
        if (student.getUUID() != null) {
            return addStudent(student.getUUID(), student);
        }
        return addStudent(UUID.randomUUID(), student);
    }

    public Student addStudent(UUID uuid, Student student) {
        student.setUUID(uuid);
        students.put(uuid, student);
        return student;
    }

    public Teacher addTeacher(Teacher teacher) {
        if (teacher.getUUID() != null) {
            return addTeacher(teacher.getUUID(), teacher);
        }
        return addTeacher(UUID.randomUUID(), teacher);
    }

    public Teacher addTeacher(UUID uuid, Teacher teacher) {
        teacher.setUUID(uuid);
        teachers.put(uuid, teacher);
        return teacher;
    }

    public boolean setStudent(UUID uuid, Student student) {
        var theStudent = students.get(uuid);
        if (theStudent == null) {
            return false;
        }
        theStudent.replaceWith(student);
        return true;
    }

    public boolean removeStudent(UUID uuid) {
        return students.remove(uuid) != null;
    }

    public Student getStudent(UUID uuid) {
        return students.get(uuid);
    }

    public Map<UUID,Student> getAllStudents() {
        return students;
    }

    public Map<UUID,Teacher> getAllTeachers() {
        return teachers;
    }

    public Teacher getTeacher(UUID uuid) {
        return teachers.get(uuid);
    }

    public Set<String> getTopics() {
        return topics;
    }

    private void populateTopics() {
        topics.add("Anthropology");
        topics.add("Archaeology");
        topics.add("Astronomy");
        topics.add("Biology");
        topics.add("Chemistry");
        topics.add("Computing");
        topics.add("Drawing");
        topics.add("Economics");
        topics.add("History");
        topics.add("Law");
        topics.add("Linguistics");
        topics.add("Literature");
        topics.add("Mathematics");
        topics.add("Medicine");
        topics.add("Music");
        topics.add("Philosophy");
        topics.add("Physics");
        topics.add("Psychology");
        topics.add("Sociology");
        topics.add("Theology");
    }

    private void populateApplicationState() {
        populateTopics();

        /*
         *  Topics offered by teachers
         */
        var physics = new Topic(
                "Physics",
                "The study of matter, energy, and the fundamental forces of nature.",
                EnumSet.allOf(Level.class));

        var math = new Topic(
                "Mathematics",
                "The study of numbers, quantity, structure, space, and change.",
                EnumSet.of(Level.Intermediate, Level.Advanced));

        var theology = new Topic(
                "Theology",
                "The study of the nature of the divine.",
                EnumSet.of(Level.Beginner, Level.Intermediate));

        /*
         *  Create and register teachers
         */
        var albert = addTeacher(UUID.fromString("2b7da5cb-a2ab-4077-be57-2b75bfc9f67b"), new Teacher("Albert", "Einstein", "einstein@emc2.org", "albert"));
        albert.addLanguage("German");
        albert.addLanguage("English");
        albert.setDescription("I am a theoretical physicist working at the Swiss Patent Office in Bern.");
        albert.addTimeslot(LocalDate.now().plusDays(1), LocalTime.now().plusHours(1).getHour());
        albert.addTimeslot(LocalDate.now().plusDays(1), LocalTime.now().plusHours(3).getHour());
        albert.addTimeslot(LocalDate.now().plusDays(2), LocalTime.now().getHour());
        albert.addTopic(physics);
        albert.addTopic(math);
        albert.setHourlyFee(40);

        var isaac = addTeacher(UUID.fromString("f3b7d1b1-1b7b-4b7b-8b7b-1b7b7b7b7b7b"), new Teacher("Isaac", "Newton", "newton@jedi.edu", "isaac"));
        isaac.addLanguage("English");
        isaac.setDescription("I am an English mathematician, physicist, and astronomer.");
        isaac.addTimeslot(LocalDate.now().plusDays(4), LocalTime.now().plusHours(4).getHour());
        isaac.addTimeslot(LocalDate.now().plusDays(5), LocalTime.now().plusHours(3).getHour());
        physics = physics.clone();
        physics.setDescription("Studium materiae, energiae, et virium fundamentalium naturae.");
        isaac.addTopic(physics);
        math = math.clone();
        math.setDescription("Studium numerorum, quantitatis, structurae, spatii, mutationis.");
        isaac.addTopic(math);
        isaac.rate(5);
        isaac.rate(4);

        var martin = addTeacher(UUID.fromString("9d6d81bb-9274-421d-a454-0f227037a348"), new Teacher("Martin", "Luther", "luther@king.com", "martin"));
        martin.addTopic(theology);
        martin.addLanguage("German");
        martin.setDescription("I am a German professor of theology and a seminal figure in the Protestant Reformation.");
        martin.addTimeslot(LocalDate.now(), LocalTime.now().plusHours(1).getHour());

        /*
         *  Topic interests expressed by students
         */
        physics = new Topic("Physics", null, Level.Advanced);
        math = new Topic("Mathematics", null, Level.Intermediate);
        theology = new Topic("Theology", null, Level.Beginner);

        /*
        /*  Create and register students
         */
        var paul = addStudent(UUID.fromString("b8d0c81d-e1c6-4708-bd02-d218a23e4805"), new Student("Paul", "Dirac", "dirac@quantum.org", "paul"));
        paul.addLanguage("English");
        paul.addLanguage("French");
        paul.addInterest(physics);
        paul.addInterest(theology);

        var robert = addStudent(UUID.fromString("0ab2ec68-c574-4d81-bed0-a93c31fab1c0"), new Student("Robert", "Oppenheimer", "oppenheimer@atomic.com", "robert"));
        robert.addLanguage("English");
        robert.addLanguage("French");
        robert.addLanguage("German");
        robert.addInterest(physics);
        robert.addInterest(math);

        var richard = addStudent(UUID.fromString("5d53a98b-53a8-4580-adc1-28067b37582a"), new Student("Richard", "Feynman", "feynman@diagrams.net", "richard"));
        richard.addLanguage("English");
        richard.addInterest(physics);
        richard.addInterest(math);
        richard.addInterest(theology);

        /*
         *  Create and book lessons
         */
        var topics = albert.getTopics();
        var topic = topics.get(0);
        var level = Level.Advanced;
        var timeslot = albert.getTimeslots().first();
        var lesson = new Lesson(timeslot, topic, level);
        paul.deposit(2*albert.getHourlyFee());
        lesson.book(albert, paul);
    }
}
