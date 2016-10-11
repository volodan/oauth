package sk.ness.ssooauth.dto;

import java.util.List;

/**
 * Pojo to parse JWT token from JSON
 * @author volodan
 *
 */
public class JwtTokenPojo {

	private List<String> aud;
	private String user_name;
	private List<String> scope;
	private String ati;
	private Long exp;
	private List<String> authorities;
	private String jti;
	private String client_id;
	
	public List<String> getAud() {
		return aud;
	}
	public void setAud(List<String> aud) {
		this.aud = aud;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public List<String> getScope() {
		return scope;
	}
	public void setScope(List<String> scope) {
		this.scope = scope;
	}
	public Long getExp() {
		return exp;
	}
	public void setExp(Long exp) {
		this.exp = exp;
	}
	public List<String> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	public String getAti() {
		return ati;
	}
	public void setAti(String ati) {
		this.ati = ati;
	}
	
}
