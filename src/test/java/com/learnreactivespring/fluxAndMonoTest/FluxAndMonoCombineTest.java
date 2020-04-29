package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge(){
VirtualTimeScheduler.getOrSet();
        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        Flux<String> merge = Flux.merge(stringFlux1, stringFlux2);

        StepVerifier.withVirtualTime(()->merge.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6).verifyComplete();

    }

    @Test
    public void combineUsingMerge_concat(){
        VirtualTimeScheduler.getOrSet();
        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        Flux<String> merge = Flux.concat(stringFlux1, stringFlux2);

        StepVerifier.withVirtualTime(()->merge.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
//        StepVerifier.create(merge.log())
//                .expectNextCount(6).verifyComplete();

    }

    @Test
    public void combineUsingMerge_zip(){
        VirtualTimeScheduler.getOrSet();
        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        Flux<String> merge = Flux.zip(stringFlux1, stringFlux2,(t1,t2)->{
            return t1.concat(t2);
        });

        StepVerifier.withVirtualTime(()->merge.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(3)
                .verifyComplete();
//        StepVerifier.create(merge.log())
//                .expectNextCount(3).verifyComplete();

    }
}
