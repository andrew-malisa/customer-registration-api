package com.vodacom.customerregistration.api.domain;

import java.util.UUID;

public class AgentTestSamples {

    public static Agent getAgentSample1() {
        return new Agent().id(UUID.fromString("11111111-1111-1111-1111-111111111111")).phoneNumber("phoneNumber1");
    }

    public static Agent getAgentSample2() {
        return new Agent().id(UUID.fromString("22222222-2222-2222-2222-222222222222")).phoneNumber("phoneNumber2");
    }

    public static Agent getAgentRandomSampleGenerator() {
        return new Agent().id(UUID.randomUUID()).phoneNumber(UUID.randomUUID().toString());
    }
}
