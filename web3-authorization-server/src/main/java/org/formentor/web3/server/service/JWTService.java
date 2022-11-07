package org.formentor.web3.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.formentor.web3.server.model.Signature;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.security.interfaces.RSAKey;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Slf4j
public class JWTService {

    private final Algorithm algorithm;
    private final NFTService nftService;

    public JWTService(Supplier<Optional<RSAKey>> keyPairSupplier, NFTService nftService) {
         algorithm = keyPairSupplier.get()
                 .map(Algorithm::RSA256)
                 .orElse(Algorithm.none());
         this.nftService = nftService;
    }

    public String buildJWT(Signature signature) {
        // Validate signature
        if (!isSignatureValid(signature)) {
            throw new InvalidSignatureException("Invalid Signature");
        }

        // Map NFT's with scopes
        String[] scope = nftService.ownedBy(signature.getIdentity());

        // Build JWT
        return JWT.create()
                .withSubject(signature.getIdentity())
                .withArrayClaim("scope", scope)
                .withExpiresAt(Instant.now().plusSeconds(5 * 60))
                .sign(algorithm);
    }

    private boolean isSignatureValid(Signature signature) {
        if (signature.getIdentity() == null || signature.getMessage() == null) {
            return false;
        }
        try {
            BigInteger signedBy = Sign.signedPrefixedMessageToKey(
                    signature.getMessage().getBytes(),
                    new Sign.SignatureData(
                            Numeric.hexStringToByteArray(signature.getV()),
                            Numeric.hexStringToByteArray(signature.getR()),
                            Numeric.hexStringToByteArray(signature.getS())
                    )
            );

            return Numeric.cleanHexPrefix(signature.getIdentity()).equalsIgnoreCase(Keys.getAddress(signedBy));
        } catch (SignatureException e) {
            log.error("Errors when validating signature", e);
            return false;
        }
    }
}
