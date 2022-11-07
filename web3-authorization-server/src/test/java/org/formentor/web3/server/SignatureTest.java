package org.formentor.web3.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.formentor.web3.server.model.Signature;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

public class SignatureTest {
    private final static String SAMPLE_MESSAGE = "Random message";
    private static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
    }
    @Test
    void createSignature() throws JsonProcessingException {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray("0xca01c7436972d634f427485ff0eeb89b817b31d4ce88d2906df210ee95a032fe"));
        Sign.SignatureData signatureData =
                Sign.signPrefixedMessage(SAMPLE_MESSAGE.getBytes(), ecKeyPair);

        final Signature signature = Signature.builder()
                .identity(Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair)))
                .message(SAMPLE_MESSAGE)
                .v(Numeric.toHexString(signatureData.getV()))
                .r(Numeric.toHexString(signatureData.getR()))
                .s(Numeric.toHexString(signatureData.getS()))
                .build();

        System.out.println(mapper.writeValueAsString(signature));
    }
}
