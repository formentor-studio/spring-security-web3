package org.formentor.web3.dapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;

public class DeployNFTCollectionTest {

    private static final String ERC721_interfaceID_asString = "0x80ac58cd";
    private static final byte[] ERC721_interfaceID = Numeric.hexStringToByteArray(ERC721_interfaceID_asString);

    private static final String NODE_HOST = "http://127.0.0.1:8545";
    private static final long CHAIN_ID = 1337;
    private static final ContractGasProvider gasProvider = new DefaultGasProvider();

    private static final String OWNER_PRIVATE_KEY = "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"; // Account added by the genesis of the Besu/Ethereum node deployed by the Docker image of the project.
    private static final Credentials OWNER_CREDENTIALS = Credentials.create(OWNER_PRIVATE_KEY);

    private static Web3j web3j;

    @BeforeAll
    static void setUp() {
        web3j = Web3j.build(new HttpService(NODE_HOST));
    }
    /*
    Level-A contract address: 0x619a83c9368ada9ffb98c3f14b662724dd19e943
    Level-A user account: (0x9dbb7fb7914c7900b07237535f70214fdae1962e:0xd6fa0ff5ab0683ec909e02c81b78636e589ba7b7bf9d50dbfd7a18535a4de94)
    Level-B contract address: 0xc83003b2ad5c3ef3e93cc3ef0a48e84dc8dbd718
    Level-B user account: (0xd22549ecf86e436a74238da3264151887e66aa2e:0xca01c7436972d634f427485ff0eeb89b817b31d4ce88d2906df210ee95a032fe)
     */
    @Disabled // Integration test that requires a local Node running
    @Test
    void supportsInterface() throws Exception {
        final ERC721Mintable erc721Mintable = deploy("Level-A", "A", OWNER_CREDENTIALS).get();
        assertTrue(erc721Mintable.supportsInterface(ERC721_interfaceID).send());
    }

    @Disabled // Integration test that requires a local Node running
    @Test
    void deployExample() throws Exception {
        Credentials user;
        ERC721Mintable erc721Mintable;

        user = Credentials.create(Keys.createEcKeyPair()); // Owns a NFT of the collection "Level-A"
        erc721Mintable = deploySandbox("Level-A", "A", OWNER_CREDENTIALS, user).get();
        String actualOwner = erc721Mintable.ownerOf(BigInteger.ZERO).send();
        assertEquals(user.getAddress(), actualOwner);
        System.out.printf("Level-A contract address: %s%n", erc721Mintable.getContractAddress());
        System.out.printf("Level-A user account: (%s:%s)%n", user.getAddress(), Numeric.toHexStringWithPrefix(user.getEcKeyPair().getPrivateKey()));

        user = Credentials.create(Keys.createEcKeyPair()); // Owns a NFT of the collection "Level-B"
        erc721Mintable = deploySandbox("Level-B", "B", OWNER_CREDENTIALS, user).get();
        actualOwner = erc721Mintable.ownerOf(BigInteger.ZERO).send();
        assertEquals(user.getAddress(), actualOwner);
        System.out.printf("Level-B contract address: %s%n", erc721Mintable.getContractAddress());
        System.out.printf("Level-B user account: (%s:%s)%n", user.getAddress(), Numeric.toHexStringWithPrefix(user.getEcKeyPair().getPrivateKey()));
    }

    private CompletableFuture<ERC721Mintable> deploySandbox(String name, String symbol, Credentials creator, Credentials nftOwner) {
        return deploy(name, symbol, creator) // Deploys NFT collection
                .thenCompose(contract -> {
                    contract.mint("ipfs://QmSxd3").sendAsync().join();
                    return CompletableFuture.completedFuture(contract);
                }) // Mints first NFT
                .thenCompose(contract -> {
                    contract.transferFrom(OWNER_CREDENTIALS.getAddress(), nftOwner.getAddress(), BigInteger.ZERO).sendAsync().join();
                    return CompletableFuture.completedFuture(contract);
                }); // Transfers NFT to user_LevelA
    }

    private CompletableFuture<ERC721Mintable> deploy(String name, String symbol, Credentials creator) {
        return ERC721Mintable.deploy(
                web3j,
                new RawTransactionManager(web3j, creator, CHAIN_ID, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, 500),
                gasProvider,
                name,symbol).sendAsync();
    }
}
