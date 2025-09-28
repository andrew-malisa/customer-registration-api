package com.vodacom.customerregistration.api;

import com.vodacom.customerregistration.api.config.AsyncSyncConfiguration;
import com.vodacom.customerregistration.api.config.EmbeddedElasticsearch;
import com.vodacom.customerregistration.api.config.EmbeddedSQL;
import com.vodacom.customerregistration.api.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { CustomerRegistrationSystemApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
