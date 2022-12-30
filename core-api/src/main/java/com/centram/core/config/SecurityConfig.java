package com.centram.core.config;

import com.centram.core.filter.RequestFilter;
import com.centram.core.service.ThirdPartyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                    .antMatchers("/api/integration", "/api/integration/**", "/app-ws-notification", "/app-ws-notification/**", "/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/v1/user/sign-in", "/api/v1/user/forgot-password", "/api/v1/user/reset-password", "/api/v1/misc/request-demo").permitAll()
                    //.antMatchers("/**").permitAll()
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
        private ThirdPartyUserService thirdPartyUserService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        /*@Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            //System.out.println("demoCentramPass => " + passwordEncoder.encode("demoCentramPass"));
            auth.userDetailsService(thirdPartyUserService).passwordEncoder(passwordEncoder);
        }*/

        /*@Bean
        public AuthenticationFailureHandler authenticationFailureHandler() {
            return new CustomAuthenticationFailureHandler();
        }*/

        /*@Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .antMatcher("/api/integration/**")
                    .cors().and().csrf().disable()
                    .httpBasic()
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated();
        }*/

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

        /*@Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth)
                throws Exception
        {
            auth.inMemoryAuthentication()
                    .withUser("admin")
                    .password(passwordEncoder().encode("password"))
                    .roles("USER");
        }*/

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(thirdPartyUserService);
        }
    }
}