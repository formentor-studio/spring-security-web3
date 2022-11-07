package org.formentor.web3.security.identity;

public class IdentityTokenMalformedException extends RuntimeException {

    public IdentityTokenMalformedException(String message) {
        this(message, null);
    }

    public IdentityTokenMalformedException(String message, Throwable cause) {
        super(message, cause);
    }
}
