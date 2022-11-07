package org.formentor.web3.security.identity;

import org.springframework.security.core.AuthenticationException;

public class Web3AuthenticationException extends AuthenticationException {
    public Web3AuthenticationException(String msg) {
        super(msg);
    }
}
