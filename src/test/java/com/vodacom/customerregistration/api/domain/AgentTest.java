package com.vodacom.customerregistration.api.domain;

import static com.vodacom.customerregistration.api.domain.AgentTestSamples.*;
import static com.vodacom.customerregistration.api.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vodacom.customerregistration.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Agent.class);
        Agent agent1 = getAgentSample1();
        Agent agent2 = new Agent();
        assertThat(agent1).isNotEqualTo(agent2);

        agent2.setId(agent1.getId());
        assertThat(agent1).isEqualTo(agent2);

        agent2 = getAgentSample2();
        assertThat(agent1).isNotEqualTo(agent2);
    }



    @Test
    void regionTest() {
        Agent agent = getAgentRandomSampleGenerator();
        String regionValue = "Test Region";

        agent.setRegion(regionValue);
        assertThat(agent.getRegion()).isEqualTo(regionValue);

        agent.region(null);
        assertThat(agent.getRegion()).isNull();
    }

    @Test
    void districtTest() {
        Agent agent = getAgentRandomSampleGenerator();
        String districtValue = "Test District";

        agent.setDistrict(districtValue);
        assertThat(agent.getDistrict()).isEqualTo(districtValue);

        agent.district(null);
        assertThat(agent.getDistrict()).isNull();
    }

    @Test
    void wardTest() {
        Agent agent = getAgentRandomSampleGenerator();
        String wardValue = "Test Ward";

        agent.setWard(wardValue);
        assertThat(agent.getWard()).isEqualTo(wardValue);

        agent.ward(null);
        assertThat(agent.getWard()).isNull();
    }
}
