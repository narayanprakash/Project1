package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring boot", "Spring", "Java")
            .concatWith(Flux.error(new RuntimeException("Error occured while Running")))
                .log();

        stringFlux.subscribe(System.out::println,
                (e) -> System.err.print(e));

    }

    @Test
    public void fluxTestElements_withoutError() {
        Flux<String> stringFlux = Flux.just("Spring boot", "Spring", "Java")
//            .concatWith(Flux.error(new RuntimeException("Error occured while Running")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring boot")
                .expectNext("Spring")
                .expectNext("Java")

                .verifyComplete();

    }

    @Test
    public void fluxTestElements_withError() {
        Flux<String> stringFlux = Flux.just("Spring boot", "Spring", "Java")
                .concatWith(Flux.error(new RuntimeException("Error occured while Running")))
                .log();

        StepVerifier.create(stringFlux)
//                .expectNext("Spring boot")
//                .expectNext("Spring")
//                .expectNext("Java")
                //method used to count number of next ele..
                .expectNextCount(3)
                .expectErrorMessage("Error occured while Running")
//                .expectError(RuntimeException.class)
                .verify();

    }
}
