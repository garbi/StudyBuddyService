package ch.unil.doplab.studybuddy.rest;

import ch.unil.doplab.studybuddy.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.UUID;

@Path("/teachers")
public class TeacherResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/timeslotsOf/{teacherId}")
    public SortedSet<LocalDateTime> getTimeslotsOf(@PathParam("teacherId")UUID teacher) {
        return state.getTeacher(teacher).getTimeslots();
    }
}
