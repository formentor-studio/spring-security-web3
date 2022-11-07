# spring-security-web3
This project provides an *Authorization server* that implements *authentication* based on web3 signatures and *authorization* on NFT ownership.  

The identity is derived from a web3 identity and the granted authorities from the NFT's owned by the web3 identity.

The project is implemented in [Java](https://www.java.com/) using [Springboot framework](https://github.com/spring-projects/spring-boot)

Detailed documentation can be found in this article [Introducing Web3 to Springboot framework](https://joaquin-alfaro.medium.com/introducing-web3-to-springboot-framework-e2b88d08cd0)

## Contents
### web3-authorization-server
This module implements the Authorization server and provides the following operations:  

**GET /authorize**  
Login page, it opens Metamask to sign a message and sends the signature to *web3-authorization-server* that redirects to the callback page with a JWT.  

**POST /token**  
Receives a signature and returns the JWT with the public key and granted authorities.
### sample-resource-server
Example of API that delegates authentication in the *web3-authorization-server*

### dapp
Example of Dapp that implements a smart contract [ERC-721](https://eips.ethereum.org/EIPS/eip-721).

## Setup of web3-authorization-server
 
```yaml
# List of NFTs mapped with granted authorities
NFTs:
  - "0x619a83c9368ada9ffb98c3f14b662724dd19e943"
  - "0xc83003b2ad5c3ef3e93cc3ef0a48e84dc8dbd718"

# Host of a node in Blockchain network - It can be a proxy like infura - 
nodeHost: "http://127.0.0.1:8545"

# Chain id of the Blockchain network
chainId: 1337

# Private key of the account that will be used to create the sandbox (if necessary, when the NFTs property is null or empty)
mainAccount: "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
```
## Usage and example
1. Start a local ethereum node
```shell
# From the project directory
$ docker compose up -d
```
2. Start the authorization server
```shell
# From the project directory
$ mvn clean package
$ java -jar web3-authorization-server/target/web3-authorization-server-0.0.1-SNAPSHOT.jar
``` 
3. If there is no NFT mapped for authorization, *web3-authorization-server* will deploy and setup a sandbox for testing.  
The sandbox is composed of 2 NFT collections and 2 web3 accounts. The address of NFTs and the public and private key of the accounts are published during startup 
```
2022-11-07 11:36:27.252  INFO 8402 --- [main] f.w.s.Web3AuthorizationServerApplication : Started Web3AuthorizationServerApplication in 1.293 seconds (JVM running for 1.488)
==== Deploying Sandbox ====
NFT ("Level-A", "A") deployed with address: 0x594e68f223a390500467346151c9c7e6f9c1faea
Account owning an NFT "Level-A": 0x453a44f092d52700ae6bd33e916e1b1700975b8a:0x34fedc1583a78bc1bd451303dd02d1ff7374c1ae80c2fddc24718b4d7badc1e8
NFT ("Level-B", "B") deployed with address: 0x3a3f94950ca080d49842d4656762c1cfe51e2eeb
Account owning an NFT "Level-B": 0xb81f42739a448c5a52f0298e7cf25fa713dd77ef:0x35b74bd50b4359a6ec955fc13a5837e9c7cae605da309b434bd8bff0c557a5aa
==== Sandbox deployed ====
```
4. Open the page http://localhost:9080/authorize to get a JWT 
> Import to Metamask the private key of the sandbox if necessary