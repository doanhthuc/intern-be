package com.mgmtp.easyquizy.exception;

public class UnsatisfiableQuizConstraintsException extends RuntimeException {
    public UnsatisfiableQuizConstraintsException() {
        super("Cannot generate the quiz for the specified total time and selected categories!");
    }
}
