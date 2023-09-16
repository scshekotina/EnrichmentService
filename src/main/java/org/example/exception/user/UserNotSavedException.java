package org.example.exception.user;

public class UserNotSavedException extends RuntimeException{
    public UserNotSavedException() {
        super();
    }
    public UserNotSavedException(String message) {
        super(message);
    }
}
