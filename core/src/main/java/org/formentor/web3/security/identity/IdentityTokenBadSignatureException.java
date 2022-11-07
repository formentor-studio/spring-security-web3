package org.formentor.web3.security.identity;

public class IdentityTokenBadSignatureException extends RuntimeException {

    public IdentityTokenBadSignatureException(String message) {
        this(message, null);
    }

    public IdentityTokenBadSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

}
