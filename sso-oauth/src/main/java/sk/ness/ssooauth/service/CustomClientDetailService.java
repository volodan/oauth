package sk.ness.ssooauth.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import sk.ness.ssooauth.domain.OauthClientDetails;
import sk.ness.ssooauth.dto.CustomClientDetails;
import sk.ness.ssooauth.repository.OauthClientDetailsRepository;

/**
 * Custom implementation of client details service for spring repository
 * @author volodan
 */
@Service
public class CustomClientDetailService implements ClientDetailsService, ClientRegistrationService {

	private static final Logger LOG = LoggerFactory.getLogger(CustomClientDetailService.class);
	
	@Autowired
	private OauthClientDetailsRepository clientRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptEncoder;	
	
	@Override
	public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
		OauthClientDetails userDetails = new OauthClientDetails();
		
		populateClientDetails(clientDetails, userDetails);
		clientRepository.save(userDetails);
	}

	@Override
	public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
		// load user by ID first and then add all the provided details
		OauthClientDetails userDetails = clientRepository.findOne(clientDetails.getClientId());
		
		populateClientDetails(clientDetails, userDetails);
		clientRepository.save(userDetails);
	}

	@Override
	public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
		OauthClientDetails userDetails = clientRepository.findOne(clientId);
		
		userDetails.setClientSecret(secret);
		clientRepository.save(userDetails);

	}

	@Override
	public void removeClientDetails(String clientId) throws NoSuchClientException {
		clientRepository.delete(clientId);
	}

	@Override
	public List<ClientDetails> listClientDetails() {
		List<ClientDetails> clients = new ArrayList<ClientDetails>();
		
		for (OauthClientDetails client : clientRepository.findAll()) {
			clients.add(extractClientDetails(client));
		}
 		return clients;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		OauthClientDetails userDetails = clientRepository.findOne(clientId);
		return extractClientDetails(userDetails);
	}
	
	public void enableClient(String clientId) {
		OauthClientDetails userDetails = clientRepository.findOne(clientId);

		LOG.debug("User found: " + userDetails);
		
		userDetails.setEnabled(true);
		clientRepository.save(userDetails);
	}
	
	private void populateClientDetails(ClientDetails clientDetails, OauthClientDetails userDetails) {
		userDetails.setAccessTokenValidity(clientDetails.getAccessTokenValiditySeconds());
		userDetails.setAuthorities(clientDetails.getAuthorities() != null ? 
				StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorities()) : null);
		userDetails.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes() != null ? StringUtils
				.collectionToCommaDelimitedString(clientDetails.getAuthorizedGrantTypes()) : null);
		userDetails.setClientId(clientDetails.getClientId());
		userDetails.setClientSecret(clientDetails.getClientSecret() != null ? bCryptEncoder.encode(clientDetails.getClientSecret()) : null);
		userDetails.setRefreshTokenValidity(clientDetails.getRefreshTokenValiditySeconds());
		userDetails.setResourceIds(clientDetails.getResourceIds() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails
				.getResourceIds()) : null);
		userDetails.setScope(clientDetails.getScope() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails
				.getScope()) : null);
		userDetails.setEnabled( clientDetails instanceof CustomClientDetails ? ((CustomClientDetails)clientDetails).isEnabled() : false);
	}
	
	private ClientDetails extractClientDetails(OauthClientDetails userDetails) {
		CustomClientDetails details = new CustomClientDetails(userDetails.getClientId(), userDetails.getResourceIds(), 
				userDetails.getScope(), userDetails.getAuthorizedGrantTypes(), userDetails.getAuthorities(), null);
		details.setClientSecret(userDetails.getClientSecret());
		if (userDetails.getAccessTokenValidity() != null) {
			details.setAccessTokenValiditySeconds(userDetails.getAccessTokenValidity());
		}
		if (userDetails.getRefreshTokenValidity() != null) {
			details.setRefreshTokenValiditySeconds(userDetails.getRefreshTokenValidity());
		}
		details.setEnabled(userDetails.isEnabled());
		return details;
	}
}
