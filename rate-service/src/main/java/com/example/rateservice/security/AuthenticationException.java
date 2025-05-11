package com.example.rateservice.security;

public class AuthenticationException extends RuntimeException {
    
    private final String errorCode;
    
    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public static class InvalidCredentialsException extends AuthenticationException {
        public InvalidCredentialsException() {
            super("Invalid username or password", "INVALID_CREDENTIALS");
        }
    }
    
    public static class TokenValidationException extends AuthenticationException {
        public TokenValidationException(String message) {
            super(message, "INVALID_TOKEN");
        }
    }
    
    public static class AccountDisabledException extends AuthenticationException {
        public AccountDisabledException() {
            super("User account is disabled", "ACCOUNT_DISABLED");
        }
    }
    
    public static class AccessDeniedException extends AuthenticationException {
        public AccessDeniedException() {
            super("You do not have permission to perform this action", "ACCESS_DENIED");
        }
    }
}