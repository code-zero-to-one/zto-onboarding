package com.codezerotoone.mvp.global.api.error.docs.child;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;

public enum TestChildErrorCode implements ErrorCodeSpec {
    COMMM;

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
