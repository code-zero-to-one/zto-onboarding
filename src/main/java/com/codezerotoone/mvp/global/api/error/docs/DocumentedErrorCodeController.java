package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/error-code")
@Slf4j
public class DocumentedErrorCodeController {
    private Map<String, ? extends Collection<DocumentedErrorCodeDto>> documentedErrorCodesPerName;

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    void onApplicationReady() {
        Object entrypointApp = this.applicationContext.getBeansWithAnnotation(SpringBootApplication.class)
                .values()
                .stream()
                .findFirst()
                .orElse(null);
        if (entrypointApp == null) {
            documentedErrorCodesPerName = Collections.unmodifiableMap(new HashMap<>());
            return;
        }

        String[] applicationContextScanBasePackages =
                entrypointApp.getClass().getAnnotation(SpringBootApplication.class).scanBasePackages();

        String basePackage = applicationContextScanBasePackages.length == 0
                ? entrypointApp.getClass().getPackage().getName()
                : applicationContextScanBasePackages[0];

        try {
            Collection<? extends Class<? extends ErrorCodeSpec>> allErrorCodeSpecClasses =
                    ErrorCodeSpecScanner.findAllErrorCodeSpecClasses(basePackage);
            this.documentedErrorCodesPerName = DynamicErrorResponseGenerator.convertToErrorResponses(allErrorCodeSpecClasses);
        } catch (ErrorCodeDocsException e) {
            log.warn("Exception", e);
            this.documentedErrorCodesPerName = Collections.unmodifiableMap(new HashMap<>());
        }
        ErrorCodeMarkdownGenerator.generateDocument(this.documentedErrorCodesPerName);
    }

    @GetMapping
    public ResponseEntity<Map<String, ?>> getDocumentation() {
        return ResponseEntity.ok(this.documentedErrorCodesPerName);
    }

    @GetMapping("/download-markdown")
    public ResponseEntity<String> downloadMarkdown() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"error-code.md\"")
                .body(ErrorCodeMarkdownGenerator.getMarkdownDocumentAsString());
    }
}
