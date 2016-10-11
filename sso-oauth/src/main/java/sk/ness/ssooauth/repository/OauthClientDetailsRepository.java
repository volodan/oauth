package sk.ness.ssooauth.repository;

import org.springframework.data.repository.CrudRepository;

import sk.ness.ssooauth.domain.OauthClientDetails;

public interface OauthClientDetailsRepository extends CrudRepository<OauthClientDetails, String>{

}
