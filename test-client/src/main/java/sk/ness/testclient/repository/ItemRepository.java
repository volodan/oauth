package sk.ness.testclient.repository;

import org.springframework.data.repository.CrudRepository;

import sk.ness.testclient.domain.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {

}
