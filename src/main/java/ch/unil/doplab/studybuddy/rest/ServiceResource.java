package ch.unil.doplab.studybuddy.rest;

import ch.unil.doplab.studybuddy.domain.*;
import jakarta.ejb.Local;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.*;

@Path("/service")
public class ServiceResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/reset")
    public Response reset() {
        state.init();
        return Response.ok("StudyBuddy Service was reset at " + LocalDateTime.now()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/topics")
    public List<String> getTopics() {
        return new ArrayList<>(state.getTopics());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/affinitiesWith/{studentId}")
    public Map<UUID,Set<Affinity>> findAffinitiesWith(@PathParam("studentId") UUID id) {
        Student student = state.getStudent(id);
        var allAffinities = new TreeMap<UUID, Set<Affinity>>();
        for (var teacher : state.getAllTeachers().values()) {
            var affinities = student.findAffinitiesWith(teacher);
            if (!affinities.isEmpty()) {
                allAffinities.put(teacher.getUUID(), affinities);
            }
        }
        return allAffinities;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/bookLesson")
    public Lesson bookLesson(Lesson lesson) {
        var student = state.getStudent(lesson.getStudentID());
        var teacher = state.getTeacher(lesson.getTeacherID());
        lesson.book(teacher, student);
        return lesson;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/cancelLesson")
    public void cancelLesson(Lesson lesson) {
        var student = state.getStudent(lesson.getStudentID());
        var teacher = state.getTeacher(lesson.getTeacherID());
        lesson.cancel(teacher, student);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rateLesson/{rating}")
    public void rateLesson(Lesson lesson, @PathParam("rating") String ratingName) {
        var rating = Rating.valueOf(ratingName);
        var student = state.getStudent(lesson.getStudentID());
        var teacher = state.getTeacher(lesson.getTeacherID());
        teacher.rateLesson(lesson.getTimeslot(), rating);
        student.rateLesson(lesson.getTimeslot(), rating);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/authenticate/{username}/{password}/{role}")
    public UUID authenticate(@PathParam("username") String username, @PathParam("password") String password, @PathParam("role") String role) {
        if (role.equals(Utils.teacherRole)) {
            return state.authenticate(username, password, true);
        }
        if (role.equals(Utils.studentRole)) {
            return state.authenticate(username, password, false);
        }
        return null;
    }
}
