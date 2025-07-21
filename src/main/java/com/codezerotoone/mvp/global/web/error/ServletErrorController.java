package com.codezerotoone.mvp.global.web.error;

import com.codezerotoone.mvp.global.api.error.CommonErrorCode;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ServletErrorController implements ErrorController {

    @RequestMapping("/error")
    @Hidden
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String forwardPath = (String) request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH);
        String httpMethod = request.getMethod();
        String contentType = request.getContentType();

        Map<String, Object> detail = new HashMap<>();
        detail.put("statusCode", status);
        detail.put("path", forwardPath);
        detail.put("httpMethod", httpMethod);
        if (status == 415) {
            detail.put("contentType", contentType);
        }

        return new ResponseEntity<>(
                ErrorResponse.of(
                        // TODO: switch문으로 하는 게 좋을 것인가, 아니면 Enum 값들을 순회하는 게 좋을 것인가?
                        switch (status) {
                            case 404 -> CommonErrorCode.RESOURCE_NOT_FOUND;
                            case 405 -> CommonErrorCode.HTTP_METHOD_NOT_ALLOWED;
                            case 415 -> CommonErrorCode.UNSUPPORTED_MEDIA_TYPE;
                            default -> CommonErrorCode.INTERNAL_SERVER_ERROR;
                        },
                        detail
                ),
                HttpStatus.valueOf(status)
        );
    }
}
