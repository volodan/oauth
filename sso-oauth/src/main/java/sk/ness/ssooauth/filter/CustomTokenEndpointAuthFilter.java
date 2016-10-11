package sk.ness.ssooauth.filter;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;

import sk.ness.ssooauth.dto.JwtTokenPojo;

/**
 * Custom filter, which populates auth data from refresh token to ommit need of Oauth 2 authentication when
 * requesting new access token using refresh token
 * @author volodan
 */
public class CustomTokenEndpointAuthFilter extends TokenEndpointAuthenticationFilter {

	private static final Logger LOG = LoggerFactory.getLogger(CustomTokenEndpointAuthFilter.class);
	
	public CustomTokenEndpointAuthFilter(AuthenticationManager authenticationManager,
			OAuth2RequestFactory oAuth2RequestFactory) {
		super(authenticationManager, oAuth2RequestFactory);
	}
	
	@Override
	protected Authentication extractCredentials(HttpServletRequest request) {
		Authentication result =null;
		
		String grantType = request.getParameter("grant_type");
		if (grantType != null && grantType.equals("password")) {
			result = super.extractCredentials(request);
		} else if (grantType != null && grantType.equals("refresh_token")) {
			
			// process JWT token, populate auth object and set as 
			// pre authenticated, since user is logged in
			// we implicitly anticipate, that user is authenticated once he has valid
			// refresh token
			
		    Jwt jwtToken = JwtHelper.decode(request.getParameter("refresh_token"));
		    String claims = jwtToken.getClaims();

		    ObjectMapper objectMapper = new ObjectMapper();
		    JwtTokenPojo tokenClaims;
		    try {
		        tokenClaims = objectMapper.readValue(claims, JwtTokenPojo.class);
		        LOG.debug("Claims: " + claims);
		    } catch (Exception ex) {
		        throw new BadCredentialsException("Error parsing JWT token", ex);
		    }

		    // if OK, populate SpringContext with data
		    List<GrantedAuthority> authorities = tokenClaims.getAuthorities().stream().map(l -> new SimpleGrantedAuthority(l)).collect(Collectors.toList());
		    result = new UsernamePasswordAuthenticationToken(tokenClaims.getClient_id(), "", authorities);
		}
		return result;
	}
	


}
