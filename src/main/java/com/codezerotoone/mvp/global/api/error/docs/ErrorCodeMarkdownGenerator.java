package com.codezerotoone.mvp.global.api.error.docs;

import java.util.Collection;
import java.util.Map;

class ErrorCodeMarkdownGenerator {
    private static String generatedDocumentCache = null;

    public static void generateDocument(
            Map<String, ? extends Collection<DocumentedErrorCodeDto>> documentedErrorCodesPerName
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Error Code 명세\n");

        documentedErrorCodesPerName.forEach((k, v) -> {
            sb.append("## ").append(k).append('\n');
            sb.append("| Error Code | Error Name | Status Code | Message | Description |\n");
            sb.append("| --- | --- | --- | -- | --- |\n");
            v.forEach((dto) -> {
                StaticErrorCodeDto staticErrorCodeDto = dto.getErrorResponse();
                sb.append("| ").append(staticErrorCodeDto.errorCode())
                        .append(" | ").append(staticErrorCodeDto.errorName())
                        .append(" | ").append(staticErrorCodeDto.statusCode())
                        .append(" | ").append(staticErrorCodeDto.message())
                        .append(" | ").append(dto.getDescription())
                        .append(" |\n");
            });
        });

        generatedDocumentCache = sb.toString();
    }

    public static String getMarkdownDocumentAsString() {
        return generatedDocumentCache;
    }
}
