package br.edu.ifpb.GastoZen.config;

import br.edu.ifpb.GastoZen.dto.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@RestController
public class HandlerConfig {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new br.edu.ifpb.GastoZen.config.ErrorResponse(ex.getMessage()));
    }
}