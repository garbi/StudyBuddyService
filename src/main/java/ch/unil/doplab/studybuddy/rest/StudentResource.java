package ch.unil.doplab.studybuddy.rest;

import ch.unil.doplab.studybuddy.business.ApplicationState;
import ch.unil.doplab.studybuddy.business.Student;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/students")
public class StudentResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> getAllStudents() {
        return new ArrayList<>(state.getAllStudents().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Student getStudent(@PathParam("id") UUID id) {
        return state.getStudent(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean setStudent(@PathParam("id") UUID id, Student student) {
        return state.setStudent(id, student);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Student addStudent(Student student) {
        state.addStudent(student);
        return student;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeStudent(@PathParam("id") UUID id) {
        return state.removeStudent(id);
    }
}
