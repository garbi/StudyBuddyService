package ch.unil.doplab.studybuddy.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class ApplicationState {

    private Map<UUID, Student> students;
    private Map<UUID, Teacher> teachers;
    private Map<String, UUID> users;
    private Set<String> topics;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        students = new TreeMap<>();
        teachers = new TreeMap<>();
        users = new TreeMap<>();
        topics = new TreeSet<>();
        populateTopics();

        var allStudents = findAllStudents();
        for (var student : allStudents) {
            students.put(student.getUUID(), student);
            users.put(student.getUsername(), student.getUUID());
        }

        var allTeachers = findAllTeachers();
        for (var teacher : allTeachers) {
            teachers.put(teacher.getUUID(), teacher);
            users.put(teacher.getUsername(), teacher.getUUID());
        }
    }

    private void clearObjects() {
        students.clear();
        teachers.clear();
        users.clear();
        topics.clear();
    }

    private void clearTables() {
        // Disable foreign key checks
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // Get all table names in the studybuddy schema
        List<String> tables = (List<String>) em
                .createNativeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'studybuddy'")
                .getResultList();

        // Avoid removing the sequence table
        tables.remove("SEQUENCE");

        // Truncate each table
        for (String table : tables) {
            em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        // Re-enable foreign key checks
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    public List<Student> findAllStudents() {
        return em.createQuery("SELECT c FROM Student c", Student.class).getResultList();
    }

    public List<Teacher> findAllTeachers() {
        return em.createQuery("SELECT c FROM Teacher c", Teacher.class).getResultList();
    }

    @Transactional
    public void clearDB() {
        clearObjects();
        clearTables();
        populateTopics();
    }

    @Transactional
    public void populateDB() {
        clearObjects();
        populateApplicationState();
        for (var student : students.values()) {
            em.persist(student);
            System.out.println("Persisted student: " + student);
        }
        for (var teacher : teachers.values()) {
            em.persist(teacher);
        }
    }

    private void testTeacherDB() {
        LocalDateTime timeslot;
        var physics = new Topic(
                "Physics",
                "The study of matter, energy, and the fundamental forces of nature.",
                EnumSet.allOf(Level.class));

        var math = new Topic(
                "Mathematics",
                "The study of numbers, quantity, structure, space, and change.",
                EnumSet.of(Level.Intermediate, Level.Advanced));

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
        em.persist(albert);
    }

    private void testStudentDB() {
        LocalDateTime timeslot;
        var physics = new Topic("Physics", null, Level.Advanced);
        var math = new Topic("Mathematics", null, Level.Intermediate);
        var theology = new Topic("Theology", null, Level.Beginner);
        var paul = addStudent(UUID.fromString("b8d0c81d-e1c6-4708-bd02-d218a23e4805"), new Student("Paul", "Dirac", "dirac@quantum.org", "paul", Utils.hashPassword("1234")));
        paul.addLanguage("English");
        paul.addLanguage("French");
        paul.addInterest(physics);
        paul.addInterest(theology);
        em.persist(paul);
    }

    @Transactional
    public void resetDB() {
        clearDB();
        populateDB();
    }

    @Transactional
    public void rateLesson(Lesson lesson, Rating rating) {
        var student = getStudent(lesson.getStudentID());
        var teacher = getTeacher(lesson.getTeacherID());
        teacher.rateLesson(lesson.getTimeslot(), rating);
        student.rateLesson(lesson.getTimeslot(), rating);
        em.merge(teacher);
        em.merge(student);
    }

    @Transactional
    public void bookLesson(Lesson lesson) {
        var student = getStudent(lesson.getStudentID());
        var teacher = getTeacher(lesson.getTeacherID());
        em.persist(lesson);
        lesson.book(teacher, student);
        em.merge(teacher);
        em.merge(student);
        System.out.println("Booked lesson: " + lesson);
    }

    @Transactional
    public void cancelLesson(Lesson lesson) {
        var student = getStudent(lesson.getStudentID());
        var teacher = getTeacher(lesson.getTeacherID());
        lesson.cancel(teacher, student);
        em.merge(teacher);
        em.merge(student);
        lesson = em.merge(lesson);
        em.remove(lesson);
    }

    @Transactional
    public Student addStudent(Student student) {
        if (student.getUUID() != null) {
            return addStudent(student.getUUID(), student);
        }
        return addStudent(UUID.randomUUID(), student);
    }

    @Transactional
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
        em.persist(student);

        return student;
    }

    @Transactional
    public Teacher addTeacher(Teacher teacher) {
        if (teacher.getUUID() != null) {
            return addTeacher(teacher.getUUID(), teacher);
        }
        return addTeacher(UUID.randomUUID(), teacher);
    }

    @Transactional
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
        em.persist(teacher);

        return teacher;
    }

    @Transactional
    public boolean setStudent(UUID uuid, Student student) {
        var theStudent = students.get(uuid);
        if (theStudent == null) {
            return false;
        }
        var username = student.getUsername();
        if (!theStudent.getUsername().equals(username)) {
            if (users.get(username) != null && !users.get(username).equals(uuid)) {
                throw new IllegalArgumentException("A user named '" + username + "' already exists");
            } else {
                users.remove(theStudent.getUsername());
                users.put(username, uuid);
            }
        }
        theStudent.replaceWith(student);
        em.merge(theStudent);
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

    @Transactional
    public boolean setTeacher(UUID uuid, Teacher teacher) {
        var theTeacher = teachers.get(uuid);
        if (theTeacher == null) {
            return false;
        }
        var username = teacher.getUsername();
        if (!theTeacher.getUsername().equals(username)) {
            if (users.get(username) != null && !users.get(username).equals(uuid)) {
                throw new IllegalArgumentException("A user named '" + username + "' already exists");
            } else {
                users.remove(theTeacher.getUsername());
                users.put(username, uuid);
            }
        }
        theTeacher.replaceWith(teacher);
        em.merge(theTeacher);

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
        topics.clear();
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
