package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names= Arrays.asList("adam","anna","Jack","Jenny");
    @Test
    public void fluxUsingIterable(){
        Flux<String> stringFlux = Flux.fromIterable(names);
        StepVerifier.create(stringFlux.log())
                .expectNext("adam","anna","Jack","Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] names =new String[]{"adam","anna","Jack","Jenny"};
        Flux<String> stringFlux = Flux.fromArray(names).concatWith(Flux.error(new RuntimeException("Exception occurred")));
        stringFlux.subscribe(System.out::println, System.err::println);
        StepVerifier.create(stringFlux)
                .expectNext("adam","anna","Jack","Jenny")
                .expectError()
                .log()
                .verify();
    }
//    @Test
//    public void fluxUsingStream(){
//        Flux<String> stringFlux = Flux.fromStream(names.stream());
//        stringFlux.subscribe(System.out::println);
//
//        StepVerifier.create(stringFlux)
//                .expectNext("adam","anna","Jack","Jenny")
//                .verifyComplete();
//    }

    @Test
    public void monoUsingJustOrEmpty(){
        Mono<String> empty = Mono.justOrEmpty(null);

        StepVerifier.create(empty.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){
        Supplier<String> stringSupplier=()->"adam";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        System.out.print(stringSupplier.get());

        StepVerifier.create(stringMono.log()).expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> range = Flux.range(1, 4);
        StepVerifier.create(range.log())
                .expectNext(1,2,3,4).verifyComplete();
    }
}
