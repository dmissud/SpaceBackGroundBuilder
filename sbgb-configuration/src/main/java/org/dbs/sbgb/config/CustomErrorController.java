package org.dbs.sbgb.config;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Hidden
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status != null ? status : 500);
        errorDetails.put("message", message != null ? message : "An unexpected error occurred");

        if (exception != null) {
            errorDetails.put("exception", exception.toString());
        }


        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            httpStatus = HttpStatus.valueOf(statusCode);
        }

        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}
