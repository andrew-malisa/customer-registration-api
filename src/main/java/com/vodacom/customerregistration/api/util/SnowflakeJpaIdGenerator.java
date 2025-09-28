package com.vodacom.customerregistration.api.util;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * JPA/Hibernate ID Generator using Snowflake algorithm
 * 
 * Usage in entities:
 * @Id
 * @GenericGenerator(name = "snowflake", strategy = "com.vodacom.customerregistration.api.util.SnowflakeJpaIdGenerator")
 * @GeneratedValue(generator = "snowflake")
 * private Long id;
 */
@Component
public class SnowflakeJpaIdGenerator implements IdentifierGenerator {
    
    private static final SnowflakeIdGenerator snowflakeGenerator = new SnowflakeIdGenerator();
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return snowflakeGenerator.nextId();
    }
}