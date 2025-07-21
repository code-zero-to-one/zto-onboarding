package com.codezerotoone.mvp.global.api.error.docs.child;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import com.codezerotoone.mvp.global.api.error.docs.ErrorCodeDocumentation;

@ErrorCodeDocumentation(name = "Test Error Code")
public enum TestNamedErrorCode implements ErrorCodeSpec {
    NOVEMBER;

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
