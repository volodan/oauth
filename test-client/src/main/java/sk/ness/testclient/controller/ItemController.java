package sk.ness.testclient.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sk.ness.testclient.domain.Item;
import sk.ness.testclient.repository.ItemRepository;

@RestController
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemRepository itemRepository;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Item> listItems() {
		List<Item> retList = new ArrayList<Item>();
		for (Item item : itemRepository.findAll()) {
			retList.add(item);
		}
		return retList;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> createItem(@RequestBody Item item) {
		itemRepository.save(item);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
