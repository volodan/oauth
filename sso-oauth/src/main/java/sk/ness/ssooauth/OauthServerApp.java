package sk.ness.ssooauth;
import java.security.KeyPair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import sk.ness.ssooauth.filter.CustomTokenEndpointAuthFilter;
import sk.ness.ssooauth.service.CustomClientDetailService;
import sk.ness.ssooauth.service.CustomUserDetailService;
import sk.ness.ssooauth.service.PersonAuthenticationManager;

@SpringBootApplication
@ComponentScan(basePackages = { "sk.ness" })
public class OauthServerApp {

	public static void main(String[] args) {
		SpringApplication.run(OauthServerApp.class, args);
	}

	@Bean
	public CustomClientDetailService customClientDetailService() {
		CustomClientDetailService clientDetailService = new CustomClientDetailService();
		return clientDetailService;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder(4); // log rounds of encoding - see BCrypt
	}

	@Configuration
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	protected static class SecurityConfig extends WebSecurityConfigurerAdapter {


		@Bean
		public UserDetailsService userDetailService() {
			return new CustomUserDetailService();
		}
		
		@Autowired
		private BCryptPasswordEncoder bCryptPasswordEncoder;
	
		
		@Override
		@Autowired // <-- This is crucial otherwise Spring Boot creates its own
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailService()).passwordEncoder(bCryptPasswordEncoder);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().httpBasic().and() 
					.authorizeRequests()
					.antMatchers("/user").permitAll();
					//.hasAuthority("oauth_admin"); // FIXME we want to have user endpoint secured!!
		}
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private CustomClientDetailService customClientDetailService;
		
		@Autowired
		private PersonAuthenticationManager authenticationManager;

	    /* create OAuth2RequestFactory instance */
	    private OAuth2RequestFactory oAuth2RequestFactory;

		@Bean
		public JwtAccessTokenConverter tokenConverter() {
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			KeyPair keyPair = new KeyStoreKeyFactory(
					new ClassPathResource("keystore.jks"), "foobar".toCharArray()).getKeyPair("test"); // FIXME generate new jk store!!!
			converter.setKeyPair(keyPair);
			return converter;
		}

		@Bean
		public JwtTokenStore tokenStore() {
			return new JwtTokenStore(tokenConverter());
		}
		
		@Bean
		public DefaultTokenServices tokenServices() {
		    DefaultTokenServices tokenServices = new DefaultTokenServices();
		    tokenServices.setTokenStore(tokenStore());
		    tokenServices.setAuthenticationManager(authenticationManager);
		    return tokenServices;
		}

		// Defines the security constraints on the token endpoints /oauth/token_key and /oauth/check_token Client credentials are
		// required to access the endpoints
		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.allowFormAuthenticationForClients().tokenKeyAccess("permitAll()") // token public key is accessible from everywhere
        		.addTokenEndpointAuthenticationFilter(new CustomTokenEndpointAuthFilter(authenticationManager, oAuth2RequestFactory));
		}
		

		// Defines the authorization and token endpoints and the token services
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	        /* assign value in OAuth2RequestFactory instance */
	        oAuth2RequestFactory = endpoints.getOAuth2RequestFactory();
			endpoints
					// Which authenticationManager should be used for the password grant
					// If not provided, ResourceOwnerPasswordTokenGranter is not configured
					.authenticationManager(authenticationManager)
					// Use JwtTokenStore and our jwtAccessTokenConverter
					.tokenStore(tokenStore()).accessTokenConverter(tokenConverter());
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// yes, we can define service of our own or just redefine SQL queries to work with client details.
			clients.withClientDetails(customClientDetailService);
		}

	}
}