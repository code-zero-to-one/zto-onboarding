package com.codezerotoone.mvp.global.api.error;

public interface ErrorCodeSpec {

    int getStatusCode();

    String getErrorCode();

    String name();

    String getMessage();
}
