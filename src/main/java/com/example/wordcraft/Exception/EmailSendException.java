package com.example.wordcraft.Exception;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String message) {
        super(message);
    }
}
