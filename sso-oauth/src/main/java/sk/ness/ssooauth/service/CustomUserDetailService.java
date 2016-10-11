package sk.ness.ssooauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;

import sk.ness.ssooauth.dto.CustomClientDetails;
import sk.ness.ssooauth.dto.CustomUserDetails;

/**
 * Custom implementation of user details service - maps stored oauth client data to user details 
 * @author volodan
 */
public class CustomUserDetailService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailService.class);

    @Autowired
    private CustomClientDetailService customClientDetailService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ClientDetails client = customClientDetailService.loadClientByClientId(username);
		if (client == null) {
			LOG.info("No client found for username: " + username);
			throw new AuthenticationCredentialsNotFoundException("Client not found for username: " + username);
		}
		boolean enabled = ((CustomClientDetails)client).isEnabled();
		
		UserDetails userDetails = new CustomUserDetails(client.getClientId(), client.getClientSecret(), // no need for client secret for pre auth scenario! 
				enabled, client.getAuthorities());
		LOG.debug("User details populated from client data: " + userDetails.toString());
		
		return userDetails;
	}

}
