package sk.ness.ssooauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import sk.ness.ssooauth.dto.CustomClientDetails;


/**
 * Custom implementation of authentication based on oauth_client_details
 * username = clientId, password = clientSecret
 *
 * @author volodan
 */
@Service
public class PersonAuthenticationManager implements AuthenticationManager {

    @Autowired
    private CustomClientDetailService customClientDetailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

    	// if is already authenticated we can omit this step and just return authentication object
    	if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
    		return authentication;
    	} else {
	    	
	        if (authentication.getPrincipal() == null || authentication.getCredentials() == null) {
	            throw new BadCredentialsException("Bad username/password provided!");
	        }
	
	        // Base auth - both principal and credentials (username, password must match with predefined ones)
	        String username = authentication.getPrincipal().toString();
	        String password = authentication.getCredentials().toString();
	
	        // if username/pwd is present, find user by username in DB and continue with credentials 
	        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
	
	            // find client by his clientId i.e. username/email, if not found exception is thrown
	            ClientDetails client;
	            try {
	                client = customClientDetailService.loadClientByClientId(username);
	            } catch (Exception e) {
	                throw new BadCredentialsException("Bad username/password provided!");
	            }
	
	    		boolean enabled = ((CustomClientDetails)client).isEnabled();
	    				
	            if(!enabled){
	                throw new BadCredentialsException("User is not enabled");
	            }
	            
	            // encode and check password against password from user - 
	            // if match, return username password token, otherwise throw an exception
	            if (bCryptPasswordEncoder.matches(password, client.getClientSecret())) {
	                Authentication auth = new UsernamePasswordAuthenticationToken(username, password, client.getAuthorities());
	
	                // if authenticated, store to security context holder as well and then return!
	                SecurityContextHolder.getContext().setAuthentication(auth);
	
	                return auth;
	            } else {
	                throw new BadCredentialsException("Bad username/password provided!");
	            }
	        }
	
	        throw new BadCredentialsException("Bad credentials provided!");
	
	    }
    }

}
