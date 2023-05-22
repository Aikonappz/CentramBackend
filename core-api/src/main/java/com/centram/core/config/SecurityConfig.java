package com.centram.core.config;

import com.centram.core.filter.RequestFilter;
import com.centram.core.service.ThirdPartyAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml.*;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Configuration
    @Order(1)
    public static class RestApiSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private RequestFilter requestFilter;

        @Autowired
        @Qualifier("userService")
        private UserDetailsService userService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
        }

        /*@Bean
        public AuthenticationFailureHandler authenticationFailureHandler() {
            return new CustomAuthenticationFailureHandler();
        }*/

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .antMatcher("/api/**")
                    .cors().and().csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/integration/api/**", "/actuator/**", "/api/**", "/api/integration", "/api/integration/**", "/app-ws-notification", "/app-ws-notification/**", "/configuration/**", "/swagger*/**", "/webjars/**", "/api-docs", "/api-docs/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/v1/user/sign-in", "/api/v1/user/sso-sign-in", "/api/v1/user/forgot-password", "/api/v1/user/reset-password", "/api/v1/misc/request-demo").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            httpSecurity.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
        }

    }

    @Configuration
    @Order(2)
    public static class ActuatorSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private ActuatorAuthProvider actuatorAuthProvider;

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(actuatorAuthProvider);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/actuator/**")
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic();
        }

    }

    @Configuration
    @Order(3)
    public static class ThirdPartyIntegrationSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private ThirdPartyAuthProvider thirdPartyAuthProvider;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/integration/api/**")
                    .cors().and().csrf().disable()
                    .httpBasic()
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();
        }

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(thirdPartyAuthProvider);
        }

    }

    @Configuration
    @Order(4)
    public static class Saml2Security extends WebSecurityConfigurerAdapter {

        @Value("${service.url}")
        private String serviceUrl;

        @Value("${saml.sp}")
        private String samlAudience;

        @Autowired
        //@Qualifier("saml")
        private SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler;

        @Autowired
        //@Qualifier("saml")
        private SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler;

        @Autowired
        private SAMLEntryPoint samlEntryPoint;

        @Autowired
        private SAMLLogoutFilter samlLogoutFilter;

        @Autowired
        private SAMLLogoutProcessingFilter samlLogoutProcessingFilter;

        @Autowired
        private SAMLAuthenticationProvider samlAuthenticationProvider;

        @Autowired
        private ExtendedMetadata extendedMetadata;

        @Autowired
        private KeyManager keyManager;

        //@Bean
        public SAMLDiscovery samlDiscovery() {
            SAMLDiscovery idpDiscovery = new SAMLDiscovery();
            return idpDiscovery;
        }

        public MetadataGenerator metadataGenerator() {
            MetadataGenerator metadataGenerator = new MetadataGenerator();
            // TODO : setting for success factor
            metadataGenerator.setEntityBaseURL(serviceUrl);
            metadataGenerator.setEntityId(samlAudience);
            metadataGenerator.setExtendedMetadata(extendedMetadata);
            metadataGenerator.setIncludeDiscoveryExtension(false);
            metadataGenerator.setKeyManager(keyManager);
            return metadataGenerator;
        }

        @Bean
        public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
            SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
            // TODO : setting for null handling
            //samlWebSSOProcessingFilter.setDefaultTargetUrl("/");
            samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
            samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(savedRequestAwareAuthenticationSuccessHandler);
            samlWebSSOProcessingFilter.setAuthenticationFailureHandler(simpleUrlAuthenticationFailureHandler);
            return samlWebSSOProcessingFilter;
        }

        @Bean
        public FilterChainProxy samlFilter() throws Exception {
            List<SecurityFilterChain> chains = new ArrayList<>();
            chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
                    samlWebSSOProcessingFilter()));
            chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"),
                    samlDiscovery()));
            chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
                    samlEntryPoint));
            chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
                    samlLogoutFilter));
            chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
                    samlLogoutProcessingFilter));
            return new FilterChainProxy(chains);
        }

        @Bean
        public MetadataGeneratorFilter metadataGeneratorFilter() {
            return new MetadataGeneratorFilter(metadataGenerator());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/**")
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/integration/api/**", "/actuator/**", "/api/**", "/api/integration", "/api/integration/**", "/app-ws-notification", "/app-ws-notification/**", "/configuration/**", "/swagger*/**", "/webjars/**", "/api-docs", "/api-docs/**", "/saml/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/v1/user/sign-in", "/api/v1/user/forgot-password", "/api/v1/user/reset-password", "/api/v1/misc/request-demo").permitAll()
                    //.antMatchers("/").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
                    .authenticationEntryPoint(samlEntryPoint).and()
                    .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
                    .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
                    .addFilterBefore(samlFilter(), CsrfFilter.class)
                    .logout()
                    .addLogoutHandler((request, response, authentication) -> {
                        try {
                            response.sendRedirect("/saml/logout");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(samlAuthenticationProvider);
        }

        /*
        Commented due to REST API ISSUE
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }*/

    }
}