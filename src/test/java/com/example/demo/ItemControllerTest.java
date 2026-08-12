package com.example.demo;

import com.example.demo.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void crudFlow() {
        // create
        Item toCreate = new Item(null, "First");
        ResponseEntity<Item> created = rest.postForEntity("/items", toCreate, Item.class);
        Assertions.assertEquals(HttpStatus.CREATED, created.getStatusCode());
        Item body = created.getBody();
        Assertions.assertNotNull(body);
        Long id = body.getId();

        // read
        ResponseEntity<Item> fetched = rest.getForEntity("/items/" + id, Item.class);
        Assertions.assertEquals(HttpStatus.OK, fetched.getStatusCode());
        Assertions.assertEquals("First", fetched.getBody().getName());

        // update
        Item updated = new Item(null, "First-updated");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Item> req = new HttpEntity<>(updated, headers);
        ResponseEntity<Item> resp = rest.exchange("/items/" + id, HttpMethod.PUT, req, Item.class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("First-updated", resp.getBody().getName());

        // delete
        ResponseEntity<Void> del = rest.exchange("/items/" + id, HttpMethod.DELETE, null, Void.class);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, del.getStatusCode());

        // not found
        ResponseEntity<Item> missing = rest.getForEntity("/items/" + id, Item.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
    }
}
