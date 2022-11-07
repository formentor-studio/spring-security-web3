// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "./ERC721.sol";
import "./Counters.sol";

contract ERC721Mintable is ERC721 {

    /// @dev This emits when an NFT is minted.
    event Minted(uint256 indexed _tokenId, string uri);

    using Counters for Counters.Counter;
    Counters.Counter private _tokenIds;

    constructor(string memory name_, string memory symbol_) ERC721(name_, symbol_) {
    }

    function mint(string memory uri) public returns (uint256) {
        uint256 newItemId = _tokenIds.current();
        _mint(msg.sender, newItemId);
        _setTokenUri(newItemId, uri);
        _tokenIds.increment();

        emit Minted(newItemId, uri);

        return newItemId;
    }
}