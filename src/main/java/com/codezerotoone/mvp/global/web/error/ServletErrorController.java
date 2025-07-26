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
                // 변경 사유: 기존의 switch도 나쁘지 않지만, 결합도가 높아 ServletErrorController가 ErrorResponse의 내부 구현에 의존하게 되므로 유지보수가 어렵다고 생각됩니다.
                ErrorResponse.of(CommonErrorCode.get(status), detail),
                HttpStatus.valueOf(status)
        );
    }
}
