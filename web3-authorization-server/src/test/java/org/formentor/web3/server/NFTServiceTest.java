package org.formentor.web3.server;

import org.formentor.web3.server.service.NFTService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NFTServiceTest {
    private static final String NODE_HOST = "http://127.0.0.1:8545";
    private static Web3j web3j;

    @BeforeAll
    static void setUp() {
        web3j = Web3j.build(new HttpService(NODE_HOST));
    }


    @Disabled // Integration test that requires a local Node running
    @Test
    void ownedBy() {
        NFTService nftService = new NFTService(web3j, new Config());
        List<String> contracts = Arrays.asList("0x619a83c9368ada9ffb98c3f14b662724dd19e943", "0xc83003b2ad5c3ef3e93cc3ef0a48e84dc8dbd718");

        contracts.forEach(nftService::registerNFT);

        String[] owned = nftService.ownedBy("0x9dbb7fb7914c7900b07237535f70214fdae1962e");

        assertEquals(1, owned.length);
        assertEquals("Level-A", owned[0]);
    }
}
