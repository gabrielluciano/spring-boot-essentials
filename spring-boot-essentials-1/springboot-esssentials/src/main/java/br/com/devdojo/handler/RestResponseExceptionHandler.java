package br.com.devdojo.handler;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class RestResponseExceptionHandler extends DefaultResponseErrorHandler {

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        System.out.println("Inside hasError");
        return super.hasError(statusCode);
    }

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        System.out.println("Handling error with status: " + response.getStatusCode());
        System.out.println("Handling error with body: " + IOUtils.toString(response.getBody(), StandardCharsets.UTF_8));
        // super.handleError(response, statusCode); // Parar sumir o stack trace
    }
}
