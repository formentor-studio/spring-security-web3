package org.formentor.web3.security.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Slf4j
public class IdentitySignature {
    private String identity;
    private String message;
    private String v;
    private String r;
    private String s;

    public boolean isValid() {
        if (identity == null || message == null) {
            return false;
        }
        try {
            BigInteger signedBy = Sign.signedPrefixedMessageToKey(
                    message.getBytes(),
                    new Sign.SignatureData(
                            Numeric.hexStringToByteArray(v),
                            Numeric.hexStringToByteArray(r),
                            Numeric.hexStringToByteArray(s)
                    )
            );

            return Numeric.cleanHexPrefix(identity).equals(Keys.getAddress(signedBy));
        } catch (SignatureException e) {
            log.error("Errors when validating signature", e);
            return false;
        }
    }
}
