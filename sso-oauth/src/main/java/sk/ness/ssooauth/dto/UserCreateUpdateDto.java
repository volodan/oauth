package sk.ness.ssooauth.dto;

import java.util.List;

/**
 * Simple POJO to marshall user data to JSON to be stored as oauth client
 * @author volodan
 */
public class UserCreateUpdateDto {

	private String username;
	private String password; // in plain text!
	private List<String> resourceIds;
	private Boolean enabled;
	private List<String> roles;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<String> resourceIds) {
		this.resourceIds = resourceIds;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "UserDto [username=" + username + ", password=[SECRET], roles=" + roles + "]";
	}
}
