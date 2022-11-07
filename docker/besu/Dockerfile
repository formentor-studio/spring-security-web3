FROM hyperledger/besu

COPY besu-genesis.json ./

# "--genesis-file=./besu-genesis.json" can be replaced by "--network=dev" to use genesis file from https://github.com/hyperledger/besu/tree/main/config/src/main/resources
ENTRYPOINT [ "besu", "--genesis-file=./besu-genesis.json" ]