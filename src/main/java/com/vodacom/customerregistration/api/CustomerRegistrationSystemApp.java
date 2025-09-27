package com.vodacom.customerregistration.api;

import com.vodacom.customerregistration.api.config.ApplicationProperties;
import com.vodacom.customerregistration.api.config.CRLFLogConverter;
import com.vodacom.customerregistration.api.config.DefaultProfileUtil;
import com.vodacom.customerregistration.api.config.EnvironmantConstants;
import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableConfigurationProperties({ LiquibaseProperties.class, ApplicationProperties.class })
public class CustomerRegistrationSystemApp {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerRegistrationSystemApp.class);

    private final Environment env;

    public CustomerRegistrationSystemApp(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (
            activeProfiles.contains(EnvironmantConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(EnvironmantConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            LOG.error(
                "You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
            );
        }
        if (
            activeProfiles.contains(EnvironmantConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(EnvironmantConstants.SPRING_PROFILE_CLOUD)
        ) {
            LOG.error(
                "You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
            );
        }
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CustomerRegistrationSystemApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String applicationName = env.getProperty("spring.application.name");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path"))
            .filter(StringUtils::isNotBlank)
            .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOG.info(
            CRLFLogConverter.CRLF_SAFE_MARKER,
            """

            ───────────────────────────────────────────────────────────────
               Application '{}' started successfully!
               Local:      {}://localhost:{}{}
               External:   {}://{}:{}{}
               Profile(s): {}
            ───────────────────────────────────────────────────────────────
            """,
            applicationName,
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );

    }
}
