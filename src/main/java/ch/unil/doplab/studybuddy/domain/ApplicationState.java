package ch.unil.doplab.studybuddy.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class ApplicationState {

    private Map<UUID, Student> students;
    private Map<UUID, Teacher> teachers;
    private Map<String, UUID> users;
    private Set<String> topics;

    @PostConstruct
    public void init() {
        students = new TreeMap<>();
        teachers = new TreeMap<>();
        users = new TreeMap<>();
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
        var username = student.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Student must have a username");
        }
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        if (student.getPassword() == null || student.getPassword().isBlank()) {
            throw new IllegalArgumentException("Student must have a password");
        }
        student.setUUID(uuid);
        students.put(uuid, student);
        users.put(username, uuid);
        return student;
    }

    public Teacher addTeacher(Teacher teacher) {
        if (teacher.getUUID() != null) {
            return addTeacher(teacher.getUUID(), teacher);
        }
        return addTeacher(UUID.randomUUID(), teacher);
    }

    public Teacher addTeacher(UUID uuid, Teacher teacher) {
        var username = teacher.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Teacher must have a username");
        }
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        if (teacher.getPassword() == null || teacher.getPassword().isBlank()) {
            throw new IllegalArgumentException("Teacher must have a password");
        }
        teacher.setUUID(uuid);
        teachers.put(uuid, teacher);
        users.put(username, uuid);
        return teacher;
    }

    public boolean setStudent(UUID uuid, Student student) {
        var theStudent = students.get(uuid);
        if (theStudent == null) {
            return false;
        }
        var username = student.getUsername();
        if (!theStudent.getUsername().equals(username) &&
                users.get(username) != null &&
                !users.get(username).equals(uuid)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        theStudent.replaceWith(student);
        return true;
    }

    public boolean removeStudent(UUID uuid) {
        var student = students.get(uuid);
        if (student == null) {
            return false;
        }
        users.remove(student.getUsername());
        students.remove(uuid);
        return true;
    }

    public Student getStudent(UUID uuid) {
        return students.get(uuid);
    }

    public Map<UUID, Student> getAllStudents() {
        return students;
    }

    public Map<UUID, Teacher> getAllTeachers() {
        return teachers;
    }

    public Teacher getTeacher(UUID uuid) {
        return teachers.get(uuid);
    }

    public boolean setTeacher(UUID uuid, Teacher teacher) {
        var theTeacher = teachers.get(uuid);
        if (theTeacher == null) {
            return false;
        }
        var username = teacher.getUsername();
        if (!theTeacher.getUsername().equals(username) &&
                users.get(username) != null &&
                !users.get(username).equals(uuid)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        theTeacher.replaceWith(teacher);
        return true;
    }

    public boolean removeTeacher(UUID uuid) {
        var teacher = teachers.get(uuid);
        if (teacher == null) {
            return false;
        }
        users.remove(teacher.getUsername());
        teachers.remove(uuid);
        return true;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public UUID authenticate(String username, String password, boolean isTeacher) {
        var uuid = users.get(username);
        if (uuid == null) {
            return null;
        }
        User user;
        if (isTeacher) {
            user = teachers.get(uuid);
        } else {
            user = students.get(uuid);
        }
        if (user == null) {
            return null;
        }
        if (!Utils.checkPassword(password, user.getPassword())) {
            return null;
        }
        return uuid;
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
        Utils.testModeOn();

        LocalDateTime timeslot;
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
        var albert = addTeacher(UUID.fromString("2b7da5cb-a2ab-4077-be57-2b75bfc9f67b"), new Teacher("Albert", "Einstein", "einstein@emc2.org", "albert", Utils.hashPassword("1234")));
        albert.addLanguage("German");
        albert.addLanguage("English");
        albert.setBiography("I am a theoretical physicist working at the Swiss Patent Office in Bern.");
        timeslot = LocalDateTime.now().minusDays(1).plusHours(1).withMinute(0).withSecond(0).withNano(0);
        albert.addTimeslot(timeslot);
        timeslot = timeslot.plusHours(1);
        albert.addTimeslot(timeslot);
        timeslot = LocalDateTime.now().plusDays(1).plusHours(3).withMinute(0).withSecond(0).withNano(0);
        albert.addTimeslot(timeslot);
        timeslot = LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0);
        albert.addTimeslot(timeslot);
        albert.addCourse(physics);
        albert.addCourse(math);
        albert.setHourlyFee(25);

        var isaac = addTeacher(UUID.fromString("f3b7d1b1-1b7b-4b7b-8b7b-1b7b7b7b7b7b"), new Teacher("Isaac", "Newton", "newton@jedi.edu", "isaac", Utils.hashPassword("1234")));
        isaac.addLanguage("English");
        isaac.setBiography("I am an English mathematician, physicist, and astronomer.");
        timeslot = LocalDateTime.now().plusDays(5).plusHours(3).withMinute(0).withSecond(0).withNano(0);
        isaac.addTimeslot(timeslot);
        timeslot = LocalDateTime.now().plusDays(4).plusHours(4).withMinute(0).withSecond(0).withNano(0);
        isaac.addTimeslot(timeslot);
        timeslot = LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0);
        isaac.addTimeslot(timeslot);
        physics = physics.clone();
        physics.setDescription("Studium materiae, energiae, et virium fundamentalium naturae.");
        isaac.addCourse(physics);
        math = math.clone();
        math.setDescription("Studium numerorum, quantitatis, structurae, spatii, mutationis.");
        isaac.addCourse(math);
        isaac.rate(Teacher.maxRating);
        isaac.rate(Teacher.maxRating - 1);

        var martin = addTeacher(UUID.fromString("9d6d81bb-9274-421d-a454-0f227037a348"), new Teacher("Martin", "Luther", "luther@king.com", "martin", Utils.hashPassword("1234")));
        martin.addCourse(theology);
        martin.addLanguage("German");
        martin.setBiography("I am a German professor of theology and a seminal figure in the Protestant Reformation.");
        timeslot = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0);
        martin.addTimeslot(timeslot);
        timeslot = LocalDateTime.now().plusDays(1).plusHours(1).withMinute(0).withSecond(0).withNano(0);
        martin.addTimeslot(timeslot);

        /*
         *  Topic interests expressed by students
         */
        physics = new Topic("Physics", null, Level.Advanced);
        math = new Topic("Mathematics", null, Level.Intermediate);
        theology = new Topic("Theology", null, Level.Beginner);

        /*
        /*  Create and register students
         */
        var paul = addStudent(UUID.fromString("b8d0c81d-e1c6-4708-bd02-d218a23e4805"), new Student("Paul", "Dirac", "dirac@quantum.org", "paul", Utils.hashPassword("1234")));
        paul.addLanguage("English");
        paul.addLanguage("French");
        paul.addInterest(physics);
        paul.addInterest(theology);

        var robert = addStudent(UUID.fromString("0ab2ec68-c574-4d81-bed0-a93c31fab1c0"), new Student("Robert", "Oppenheimer", "oppenheimer@atomic.com", "robert", Utils.hashPassword("1234")));
        robert.addLanguage("English");
        robert.addLanguage("French");
        robert.addLanguage("German");
        robert.addInterest(physics);
        robert.addInterest(math);

        var richard = addStudent(UUID.fromString("5d53a98b-53a8-4580-adc1-28067b37582a"), new Student("Richard", "Feynman", "feynman@diagrams.net", "richard", Utils.hashPassword("1234")));
        richard.addLanguage("English");
        richard.addInterest(physics);
        richard.addInterest(math);
        richard.addInterest(theology);

        /*
         *  Create and book lessons
         */
        paul.deposit(3 * albert.getHourlyFee());
        timeslot = albert.firstAvailableTimeslot();
        var topic = physics;
        var level = Level.Advanced;
        var lesson = new Lesson(timeslot, topic, level);
        lesson.book(albert, paul);

        timeslot = albert.firstAvailableTimeslot();
        lesson = new Lesson(timeslot, topic, level);
        lesson.book(albert, paul);

        Utils.testModeOff();
    }
}
