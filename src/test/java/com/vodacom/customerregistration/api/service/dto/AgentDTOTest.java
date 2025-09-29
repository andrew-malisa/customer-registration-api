package com.vodacom.customerregistration.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vodacom.customerregistration.api.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentDTO.class);
        AgentDTO agentDTO1 = new AgentDTO();
        agentDTO1.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        AgentDTO agentDTO2 = new AgentDTO();
        assertThat(agentDTO1).isNotEqualTo(agentDTO2);
        agentDTO2.setId(agentDTO1.getId());
        assertThat(agentDTO1).isEqualTo(agentDTO2);
        agentDTO2.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        assertThat(agentDTO1).isNotEqualTo(agentDTO2);
        agentDTO1.setId(null);
        assertThat(agentDTO1).isNotEqualTo(agentDTO2);
    }
}
