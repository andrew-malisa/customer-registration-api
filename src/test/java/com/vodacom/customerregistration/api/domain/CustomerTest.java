package com.vodacom.customerregistration.api.domain;

import static com.vodacom.customerregistration.api.domain.AgentTestSamples.*;
import static com.vodacom.customerregistration.api.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vodacom.customerregistration.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void regionTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        String regionValue = "Test Region";

        customer.setRegion(regionValue);
        assertThat(customer.getRegion()).isEqualTo(regionValue);

        customer.region(null);
        assertThat(customer.getRegion()).isNull();
    }

    @Test
    void districtTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        String districtValue = "Test District";

        customer.setDistrict(districtValue);
        assertThat(customer.getDistrict()).isEqualTo(districtValue);

        customer.district(null);
        assertThat(customer.getDistrict()).isNull();
    }

    @Test
    void wardTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        String wardValue = "Test Ward";

        customer.setWard(wardValue);
        assertThat(customer.getWard()).isEqualTo(wardValue);

        customer.ward(null);
        assertThat(customer.getWard()).isNull();
    }
}
