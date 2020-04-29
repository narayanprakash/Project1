package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithoutVirtualTime(){
        Flux<Long> take = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.create(take)
                .expectSubscription()
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }
//This makes the process faster.
    @Test
    public void testingWithVirtualTime(){
        VirtualTimeScheduler.getOrSet();

        Flux<Long> take = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.withVirtualTime(()->take)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }
}
