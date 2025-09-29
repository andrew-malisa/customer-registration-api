package com.vodacom.customerregistration.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.vodacom.customerregistration.api.security.*;
import static com.vodacom.customerregistration.api.security.AuthoritiesConstants.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/logout")).permitAll()
                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/account/reset/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/account/reset/finish.html")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/agents/register")).permitAll()

                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(ADMIN)
                    .requestMatchers(mvc.pattern("/api/v1/users/**")).hasAuthority(ADMIN)

                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/agents/**")).hasAnyAuthority(ADMIN, AGENT)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/agents")).hasAuthority(ADMIN)
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/v1/agents/**")).hasAnyAuthority(ADMIN, AGENT)
                    .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/agents/**")).hasAnyAuthority(ADMIN, AGENT)
                    .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/agents/**")).hasAuthority(ADMIN)

                    .requestMatchers(mvc.pattern("/api/v1/customers/**")).hasAnyAuthority(ADMIN, AGENT)

                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/activity-logs/**")).hasAnyAuthority(ADMIN, AGENT)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/activity-logs/**")).hasAuthority(ADMIN)
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/v1/activity-logs/**")).hasAuthority(ADMIN)
                    .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/activity-logs/**")).hasAuthority(ADMIN)

                    .requestMatchers(mvc.pattern("/api/account")).authenticated()
                    .requestMatchers(mvc.pattern("/api/account/**")).authenticated()
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-ui.html")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-resources/**")).permitAll()
                    .requestMatchers(mvc.pattern("/webjars/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()

                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(ADMIN)

                    .requestMatchers(mvc.pattern("/api/**")).authenticated()

                    .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
