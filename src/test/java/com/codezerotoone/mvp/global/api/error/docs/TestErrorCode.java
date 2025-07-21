package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;

public enum TestErrorCode implements ErrorCodeSpec {
    NOTHING;

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
