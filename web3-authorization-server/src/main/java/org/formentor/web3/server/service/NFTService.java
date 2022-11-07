package org.formentor.web3.server.service;

import lombok.extern.slf4j.Slf4j;
import org.formentor.web3.dapp.ERC721Mintable;
import org.formentor.web3.server.Config;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;

@Service
@Slf4j
public class NFTService {
    private static final String ERC721_interfaceID_asString = "0x80ac58cd";
    private static final byte[] ERC721_interfaceID = Numeric.hexStringToByteArray(ERC721_interfaceID_asString);
    private final String ADDRESS_ANONYMOUS = "0x0000000000000000000000000000000000000000";

    private final Web3j web3j;
    private final Config config;
    private final ContractGasProvider contractGasProvider;
    private final TransactionManager readOnlyTransactionManager;

    private final List<ERC721Mintable> NFTs;

    public NFTService(Web3j web3j, Config config) {
        this.NFTs = new ArrayList<>();
        this.web3j = web3j;
        this.config = config;
        this.contractGasProvider = new DefaultGasProvider();
        this.readOnlyTransactionManager = new ReadonlyTransactionManager(web3j, ADDRESS_ANONYMOUS);
    }

    public List<String> getNFTs() {
        return NFTs.stream().map(ERC721Mintable::getContractAddress).toList();
    }

    public ERC721Mintable registerNFT(String contractAddress) {
        final ERC721Mintable erc721Mintable = ERC721Mintable.load(contractAddress, web3j, readOnlyTransactionManager, contractGasProvider);
        try {
            if (supportsERC721(erc721Mintable)) {
                NFTs.add(erc721Mintable);
            } else {
                log.warn("NFT {} not registered as it does not support ERC721 specification", contractAddress);
            }
        } catch (Exception e) {
            log.error("NFT {} not registered due to unexpected errors", contractAddress, e);
        }

        return erc721Mintable;
    }

    public CompletableFuture<ERC721Mintable> createAndRegisterNFT(String name, String symbol, Credentials creator) {
        return ERC721Mintable.deploy(
                web3j,
                new RawTransactionManager(web3j, creator, config.getChainId(), DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, 200),
                contractGasProvider,
                name, symbol)
                .sendAsync()
                .thenApply((ERC721Mintable contract) -> {
                    NFTs.add(contract);
                    return contract;
                });
    }

    public String[] ownedBy(String address) {
        CompletableFuture<Optional<String>>[] balanceOfFutureList = NFTs.stream()
                .map(contract -> contract.balanceOf(address)
                        .sendAsync()
                        .thenApply(balance -> {
                            if (balance.compareTo(BigInteger.ZERO) > 0) {
                                try {
                                    return Optional.of(contract.name().send());
                                } catch (Exception e) {
                                    log.error("Errors when calling the function \"balanceOf\" of {}", contract.getContractAddress(), e);
                                    return Optional.empty();
                                }
                            } else {
                                return Optional.empty();
                            }
                        })
                ).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(balanceOfFutureList).join();

        return Arrays.stream(balanceOfFutureList)
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(String[]::new);
    }

    private boolean supportsERC721(ERC721Mintable erc721Mintable) throws Exception {
        return erc721Mintable.supportsInterface(ERC721_interfaceID).send();
    }
}
