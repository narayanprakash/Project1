package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

//Here Item is the object and the Type of Id is String.
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item,String>{

    Flux<Item> findByDescription(String id);
}
