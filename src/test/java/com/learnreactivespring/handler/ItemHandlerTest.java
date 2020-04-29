package com.learnreactivespring.handler;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
//@DataMongoTest this is only for repos
@DirtiesContext
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> getData() {
        return Arrays.asList(new Item(null, "Samsung Tv", 399.99),
                new Item(null, "Apple Tv", 299.99),
                new Item(null, "Samsung Watch ", 349.99),
                new Item("ABC", "LG AC", 393.99)
        );
    }

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(getData()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted Item is :" + item))
                .blockLast();
    }

    @Test
    public void getAllItemTest() {

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItemTest1() {

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> responseBody = response.getResponseBody();
                    responseBody.forEach(item -> {
                        assertTrue(item.getId() != null);
                    });
                });
    }

    @Test
    public void getAllItemTest2() {

        Flux<Item> responseBody = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(responseBody.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getItemById() {

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}", "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 393.99);


    }

    @Test
    public void getItemById_NOTfound() {

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}", "AC")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItem() {

        Item item =new Item(null,"I-Phone",234.55 );

        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo(item.getDescription())
                .jsonPath("$.price").isEqualTo(item.getPrice());
    }
    @Test
    public void deleteItem(){
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}","ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){

        Item item=new Item(null,"LG TV",234.87);
        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}","ABC")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange()
                .expectBody()
                .jsonPath("$.description",item.getDescription());
    }
    @Test
    public void updateItem_invalidId(){

        Item item=new Item(null,"LG TV",234.87);
        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}","CD")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }
@Test
    public void runTimeException(){
        webTestClient.get().uri("/fun/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message","Exception occured in runtime");
}
}
