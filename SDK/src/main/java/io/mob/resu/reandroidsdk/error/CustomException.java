package io.mob.resu.reandroidsdk.error;
 class CustomException extends Exception {

    private boolean code;
    private String exception;

    public CustomException(boolean code, String exception) {
        this.code = code;
        this.exception = exception;
    }

    public CustomException(boolean code, Throwable throwable) {
        this.code = code;
        this.exception = throwable.getMessage();
    }


    public CustomException() {
    }

    public boolean getCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
