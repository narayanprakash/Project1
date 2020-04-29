package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest //Handy Annotation for Mongo Repository
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Sumsung TV", 4000.0),
            new Item(null, "Sumsung TV", 3000.0),
            new Item(null, "Sumsung Mobile", 5000.0),
            new Item(null, "Appol Watch", 7000.0),
            new Item("A1", "Appol TV", 10000000.0)
    );

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList).log())
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item ->
                        System.out.println("Inserted Item is :" + item)
                ))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("A1"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equalsIgnoreCase("Appol TV"))
                .verifyComplete();
    }

    @Test
    public void findByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Appol TV"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {

        Item item = new Item("G1", "Google Home Mini", 20001.0);
        Mono<Item> save = itemReactiveRepository.save(item);

        StepVerifier.create(save.log())
                .expectSubscription()
                .expectNextMatches(item1 -> !item1.getId().equals(null) && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();


    }

    @Test
    public void Updatetem() {

        Flux<Item> google_home_mini = itemReactiveRepository.findByDescription("Appol TV")
                .map(item -> {
                    item.setPrice(32000.0);
                    return item;
                })
                .flatMap((item) -> {
                    return itemReactiveRepository.save(item);
                });

        StepVerifier.create(google_home_mini.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 32000.0)
                .verifyComplete();


    }

    @Test
    public void deleteItem() {

        Flux<Void> appol_tv = itemReactiveRepository.findByDescription("Appol TV")
                .flatMap(item -> itemReactiveRepository.delete(item));


        StepVerifier.create(appol_tv.log())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();


    }

}
