package com.example.catsbrowsebackendsignuplogin.exception;




public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}