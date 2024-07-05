package ch.unil.doplab.studybuddy.rest;

import ch.unil.doplab.studybuddy.domain.ApplicationState;
import ch.unil.doplab.studybuddy.domain.Student;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/service")
public class ServiceResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/reset")
    public Response reset() {
        state.init();
        return Response.ok("StudyBuddy Service was reset").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/topics")
    public List<String> getTopics() {
        return new ArrayList<>(state.getTopics());
    }

}
