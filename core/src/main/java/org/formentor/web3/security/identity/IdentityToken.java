package org.formentor.web3.security.identity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Base64;

@Slf4j
public class IdentityToken {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static byte[] decode(String token) {
        return Base64.getDecoder().decode(token);
    }

    public static String encode(IdentitySignature signature) {
        try {
            return Base64.getEncoder().encodeToString(mapper.writeValueAsString(signature).getBytes());
        } catch (JsonProcessingException e) {
            throw new IdentityTokenMalformedException("Errors encoding IdentitySignature", e);
        }
    }

    public static IdentitySignature parse(String token) {
        return parse(decode(token));
    }

    public static IdentitySignature parse(byte[] content) {
        try {
            return mapper.readValue(content, IdentitySignature.class);
        } catch (IOException e) {
            throw new IdentityTokenMalformedException("Malformed identity token", e);
        }
    }

    public static boolean isSignatureValid(IdentitySignature identitySignature) {
        if (identitySignature.getIdentity() == null || identitySignature.getMessage() == null) {
            return false;
        }
        try {
            BigInteger signedBy = Sign.signedPrefixedMessageToKey(
                    identitySignature.getMessage().getBytes(),
                    new Sign.SignatureData(
                        Numeric.hexStringToByteArray(identitySignature.getV()),
                        Numeric.hexStringToByteArray(identitySignature.getR()),
                        Numeric.hexStringToByteArray(identitySignature.getS())
                    )
            );

            return Numeric.cleanHexPrefix(identitySignature.getIdentity()).equals(Keys.getAddress(signedBy));
        } catch (SignatureException e) {
            log.error("Errors when validating signature", e);
            return false;
        }
    }

    public static IdentityTokenBuilder create() {
        return new IdentityTokenBuilder();
    }

    public static class IdentityTokenBuilder {
        private String message;
        private Credentials credentials;

        public IdentityTokenBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public IdentityTokenBuilder withCredentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public String sign() {
            try {
                Sign.SignatureData signatureData =
                        Sign.signPrefixedMessage(message.getBytes(), credentials.getEcKeyPair());

                IdentitySignature identitySignature = IdentitySignature.builder()
                        .identity(credentials.getAddress())
                        .message(message)
                        .v(Numeric.toHexString(signatureData.getV()))
                        .r(Numeric.toHexString(signatureData.getR()))
                        .s(Numeric.toHexString(signatureData.getS()))
                        .build();

                String signatureString = mapper.writeValueAsString(identitySignature);
                return Base64.getEncoder().encodeToString(signatureString.getBytes());
            } catch (Exception e) {
                throw new IdentityTokenBadSignatureException("Errors signing token", e);
            }
        }
    }
}
