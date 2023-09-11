
package org.acme;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.hibernate.exception.ConstraintViolationException;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = "Erro de validação: " + exception.getMessage();
        return Response.status(422)
                .entity(new ErrorMessage(message))
                .build();
    }
}