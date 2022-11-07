package org.formentor.web3.server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.formentor.web3.dapp.ERC721Mintable;
import org.formentor.web3.server.service.NFTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Configuration
@ConfigurationProperties
@Slf4j
public class Config {
    @Autowired
    private ApplicationContext context;

    @Getter
    @Setter
    private List<String> NFTs;

    @Getter
    @Setter
    private String mainAccount;

    @Getter
    @Setter
    private String nodeHost;

    @Getter
    @Setter
    private Long chainId;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void onApplicationReadyEvent() {
        NFTService nftService = context.getBean(NFTService.class);
        if (NFTs != null && NFTs.size() > 0) {
            NFTs.forEach(nftService::registerNFT); // Registers configured NFTs to be used as scope or authorities of Web3 identities
        } else {
            try {
                deploySandbox(nftService);
            } catch (Exception e) {
                log.error("Errors deploying sandbox", e);
            }
        }
    }

    /**
     * Returns a Supplier with the RSAKey used to sign JWT tokens
     * @return RSAkey supplier
     */
    @Bean
    Supplier<Optional<RSAKey>> keyPairSupplier() {
        /*
          IMPORTANT:
          Actually the RSAKey is generated every time the service is started.
         */
        try {
            System.out.println("==== Creating RSA key pair to sign JWT's ====");

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair rsaKeyPair = keyPairGenerator.generateKeyPair();
            String pubKey = Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded());
            String prvKey = Base64.getEncoder().encodeToString(rsaKeyPair.getPrivate().getEncoded());

            System.out.printf("-----BEGIN PUBLIC KEY-----%n%s%n-----END PUBLIC KEY-----%n", pubKey);
            System.out.printf("-----BEGIN PRIVATE KEY-----%n%s%n-----END PRIVATE KEY-----%n", prvKey);

            System.out.println("==== RSA key pair created ====");
            return () ->  Optional.of((RSAKey)rsaKeyPair.getPrivate());
        }
        catch (Exception ex) {
            log.error("Errors deriving RSAKey for signing JWT", ex);
            return Optional::empty;
        }
    }

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(nodeHost));
    }

    private void deploySandbox(NFTService nftService) throws Exception {

        System.out.println("==== Deploying Sandbox ====");

        final Credentials creatorAccount = Credentials.create(mainAccount);
        // NOTE: the "deploy and transfer" can not be executed concurrently as they are done by the same Account and both transactions would use the same NONCE
        deployNFTAndTransferTokenTo("Level-A", "A", creatorAccount, Credentials.create(Keys.createEcKeyPair()), nftService).join();
        deployNFTAndTransferTokenTo("Level-B", "B", creatorAccount, Credentials.create(Keys.createEcKeyPair()), nftService).join();

        System.out.println("==== Sandbox deployed ====");
    }

    private CompletableFuture<ERC721Mintable> deployNFTAndTransferTokenTo(String name, String symbol, Credentials creator, Credentials toAccount, NFTService nftService) {
        return nftService.createAndRegisterNFT(name, symbol, creator) // Deploys NFT collection
                .thenCompose(contract -> {
                    contract.mint("ipfs://QmSxd3").sendAsync().join();
                    return CompletableFuture.completedFuture(contract);
                }) // Mints first NFT
                .thenCompose(contract -> {
                    contract.transferFrom(creator.getAddress(), toAccount.getAddress(), BigInteger.ZERO).sendAsync().join();
                    System.out.printf("NFT (\"%s\", \"%s\") deployed with address: %s%n", name, symbol, contract.getContractAddress());
                    System.out.printf("Account owning an NFT \"%s\": %s:%s%n", name, toAccount.getAddress(), Numeric.toHexStringWithPrefix(toAccount.getEcKeyPair().getPrivateKey()));

                    return CompletableFuture.completedFuture(contract);
                }); // Transfers NFT to account
    }
}
