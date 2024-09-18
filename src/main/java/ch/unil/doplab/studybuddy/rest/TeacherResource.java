package ch.unil.doplab.studybuddy.rest;

import ch.unil.doplab.studybuddy.domain.ApplicationState;
import ch.unil.doplab.studybuddy.domain.Teacher;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/teachers")
public class TeacherResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/timeslotsOf/{teacherId}")
    public List<LocalDateTime> getTimeslotsOf(@PathParam("teacherId")UUID teacher) {
        return state.getTeacher(teacher).getTimeslots();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Teacher> getAllTeachers() {
        return new ArrayList<>(state.getAllTeachers().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Teacher getTeacher(@PathParam("id") UUID id) {
        return state.getTeacher(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean setTeacher(@PathParam("id") UUID id, Teacher teacher) {
        return state.setTeacher(id, teacher);
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Teacher addTeacher(Teacher teacher) {
        state.addTeacher(teacher);
        return teacher;
    }



}
