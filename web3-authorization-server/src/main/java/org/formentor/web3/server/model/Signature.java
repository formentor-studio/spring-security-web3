package org.formentor.web3.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Signature {
    private String identity;
    private String message;
    private String v;
    private String r;
    private String s;
}
