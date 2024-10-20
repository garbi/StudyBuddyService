package ch.unil.doplab.studybuddy.rest;

import ch.unil.doplab.studybuddy.domain.ExceptionDescription;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger log = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        log.severe("Exception caught: " + exception.getMessage());

        ExceptionDescription description = new ExceptionDescription(
                exception.getClass().getName(),
                exception.getMessage()
        );

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(description)
                .build();
    }
}