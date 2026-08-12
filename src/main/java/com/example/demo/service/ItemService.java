package com.example.demo.service;

import com.example.demo.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {
    private final ConcurrentMap<Long, Item> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public List<Item> listAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Item create(Item item) {
        long id = counter.getAndIncrement();
        Item created = new Item(id, item.getName());
        store.put(id, created);
        return created;
    }

    public Optional<Item> update(Long id, Item item) {
        return Optional.ofNullable(store.computeIfPresent(id, (k, v) -> {
            v.setName(item.getName());
            return v;
        }));
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
