package sk.ness.ssooauth.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="oauth_client_details")
public class OauthClientDetails {

	@Id
	@Column(name = "client_id", length=256)
	private String clientId;
	
	@Column(name = "resource_ids", length=256)
	private String resourceIds;
	
	@Column(name = "client_secret", length=256)
	private String clientSecret;
	
	@Column(name = "scope", length=256)
	private String scope;
	
	@Column(name = "authorized_grant_types", length=256)
	private String authorizedGrantTypes;
	
	@Column(name = "authorities", length=256)
	private String authorities;
	
	@Column(name = "access_token_validity")
	private Integer accessTokenValidity;
	
	@Column(name = "refresh_token_validity")
	private Integer refreshTokenValidity;
	
	@Column(name = "enabled")
	private boolean enabled;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}

	public void setAccessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	public Integer getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	public void setRefreshTokenValidity(Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "OauthClientDetails [clientId=" + clientId + ", resourceIds=" + resourceIds + ", authorities="
				+ authorities + ", enabled=" + enabled + "]";
	}
	
}
