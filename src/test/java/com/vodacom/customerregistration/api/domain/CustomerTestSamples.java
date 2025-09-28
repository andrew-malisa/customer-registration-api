package com.vodacom.customerregistration.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer().id(1L).firstName("firstName1").lastName("lastName1").nidaNumber("nidaNumber1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id(2L).firstName("firstName2").lastName("lastName2").nidaNumber("nidaNumber2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .nidaNumber(UUID.randomUUID().toString());
    }
}
