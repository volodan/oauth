package sk.ness.ssooauth.dto;

/**
 * Wrapper for username passed in JSON of client to be deleted.
 * @author volodan
 */
public class UserDeleteDto {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
