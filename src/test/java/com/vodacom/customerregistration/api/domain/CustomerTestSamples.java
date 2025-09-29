package com.vodacom.customerregistration.api.domain;

import java.util.UUID;

public class CustomerTestSamples {

    public static Customer getCustomerSample1() {
        return new Customer().id(UUID.fromString("11111111-1111-1111-1111-111111111111")).firstName("firstName1").lastName("lastName1").nidaNumber("nidaNumber1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id(UUID.fromString("22222222-2222-2222-2222-222222222222")).firstName("firstName2").lastName("lastName2").nidaNumber("nidaNumber2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(UUID.randomUUID())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .nidaNumber(UUID.randomUUID().toString());
    }
}
