package com.vodacom.customerregistration.api.config;

import com.vodacom.customerregistration.api.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Snowflake ID generation
 */
@Configuration
public class SnowflakeConfiguration {
    
    @Value("${app.snowflake.machine-id:#{null}}")
    private Long machineId;
    
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        if (machineId != null) {
            return new SnowflakeIdGenerator(machineId);
        }
        return new SnowflakeIdGenerator();
    }
}