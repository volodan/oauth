package sk.ness.ssooauth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sk.ness.ssooauth.dto.CustomClientDetails;
import sk.ness.ssooauth.dto.UserCreateUpdateDto;
import sk.ness.ssooauth.dto.UserDeleteDto;
import sk.ness.ssooauth.service.CustomClientDetailService;

/**
 * Controller handles request from other services to process user data and store them as clients for further Oauth2 processes
 * @author volodan
 */
@RestController
@RequestMapping("/user")
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
		
	public static final Integer ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60; // 1 day
	
	public static final Integer REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 *60; // 1 week 

	@Autowired
	private CustomClientDetailService clientDetailService;
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> createUser(@RequestBody UserCreateUpdateDto userDto) {
		
		// validate passed data and if OK set client details
		validateValue(userDto.getUsername(), "username");
		validateValue(userDto.getPassword(), "password");
		
		CustomClientDetails clientDetails = new CustomClientDetails();
		clientDetails.setClientId(userDto.getUsername()); // clientId = username
		clientDetails.setClientSecret(userDto.getPassword()); // secret = password; encoded in client detail service

		// if roles passed - set them as granted authorities
		if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
			clientDetails.setAuthorities(populateAuthorities(userDto));
		}
		
		clientDetails.setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
		clientDetails.setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
		clientDetails.setScope(Arrays.asList("read", "write"));
		clientDetails.setAuthorizedGrantTypes(Arrays.asList("password", "refresh_token"));
		clientDetails.setResourceIds(userDto.getResourceIds());
		clientDetails.setEnabled(userDto.getEnabled());
		
		// now store client with prepopulated data
		clientDetailService.addClientDetails(clientDetails);
		
		LOG.debug(userDto.getUsername() + " successfully stored!");
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUser(@RequestBody UserDeleteDto userDto) {
		validateValue(userDto.getUsername(), "username");
	
		// delete user with given clientId = username
		clientDetailService.removeClientDetails(userDto.getUsername());
		LOG.debug(userDto.toString() + " successfully deleted!");
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<String> updateUser(@RequestBody UserCreateUpdateDto userDto) {
		
		// validate passed data and if OK set client details
		validateValue(userDto.getUsername(), "username");
		
		if (!StringUtils.isEmpty(userDto.getPassword())) {
			LOG.debug("Updating password for client: " + userDto.getUsername());
			clientDetailService.updateClientSecret(userDto.getUsername(), userDto.getPassword());
		}
		
		// if roles passed - set them as granted authorities
		if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
			// find client, and if we passed new password or roles, change them
			BaseClientDetails clientDetails = (BaseClientDetails) (clientDetailService.loadClientByClientId(userDto.getUsername()));
			LOG.debug("Client: " + clientDetails.toString() + " found!");
			clientDetails.setAuthorities(populateAuthorities(userDto));
			LOG.debug("Updating roles for client: " + clientDetails.getClientId());
			clientDetailService.updateClientDetails(clientDetails); // roles update has different method
		}
		
		// now store client with prepopulated data
		LOG.debug(userDto.getUsername() + " successfully updated!");
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/enable", method = RequestMethod.PUT)
	public ResponseEntity<HttpStatus> enableUser(@RequestBody UserCreateUpdateDto userDto){
		validateValue(userDto.getUsername(), "username");
		BaseClientDetails clientDetails = (BaseClientDetails) (clientDetailService.loadClientByClientId(userDto.getUsername()));
		clientDetails.addAdditionalInformation("enabled", true);
		clientDetailService.updateClientDetails(clientDetails);
		LOG.debug(userDto.getUsername() + " successfully enabled!");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private void validateValue(String value, String name) {
		if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException(name + " is empty!");
		}
	}
	
	private Set<GrantedAuthority> populateAuthorities(UserCreateUpdateDto userDto) {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (String role : userDto.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
