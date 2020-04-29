package com.learnreactivespring.Controllers;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
//@DataMongoTest this is only for repos
@DirtiesContext
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MongoOperations mongoOperations;

    @BeforeEach
    public void setup(){

        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(20000).capped());

        Flux<ItemCapped> map = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item :" + i, 100.00 + i))
                .take(5);

        itemReactiveCappedRepository.insert(map)
             .doOnNext(itemCapped -> {
                 System.out.println("Inserted Item in setUp " + itemCapped);
             })
                .blockLast();

    }

    @Test
    public void testStreamAllItem(){
        Flux<ItemCapped> take = webTestClient.get()
                .uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(take.log())
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
