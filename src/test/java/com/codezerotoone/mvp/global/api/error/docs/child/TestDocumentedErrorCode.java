package com.codezerotoone.mvp.global.api.error.docs.child;

import com.codezerotoone.mvp.global.api.error.docs.DocumentedErrorCodeSpec;

public enum TestDocumentedErrorCode implements DocumentedErrorCodeSpec {
    NOTHING;

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public String getErrorCode() {
        return "";
    }

    @Override
    public String getMessage() {
        return "";
    }
}
