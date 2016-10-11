package sk.ness.ssooauth.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom implementation of user details to be used for authentication
 * of Oauth2 requests
 * @author volodan
 */
public class CustomUserDetails implements UserDetails {
	
	private static final long serialVersionUID = -4748400110652585649L;
	
	private String username;
	private String password;
	private boolean enabled;
	private Collection<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public CustomUserDetails() {
		super();
	}

	public CustomUserDetails(String username, String password, boolean enabled, Collection<GrantedAuthority> authorities) {
		super();
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "CustomUserDetails [username=" + username + ", enabled=" + enabled
				+ ", authorities=" + authorities + "]";
	}

	
}
