package sk.ness.testclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@SpringBootApplication
@EnableResourceServer
public class TestClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestClientApplication.class, args);
    }

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class SecurityConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("test_client");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .headers()
                    .frameOptions().disable()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/item/create").hasAuthority("user") //just for test
                    .anyRequest().permitAll() // all other requests are permitted to all
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        }
    }
}