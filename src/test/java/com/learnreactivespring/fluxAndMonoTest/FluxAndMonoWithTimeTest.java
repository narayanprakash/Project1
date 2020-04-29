package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() {
        Flux<Long> interval = Flux.interval(Duration.ofMillis(200)).log();//starts from 0 -> .....
        interval.subscribe((e) -> System.out.println("value is : " + e));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void infiniteSequenceMap_withDelay() {
        Flux<Integer> interval = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(l->new Integer(l.intValue()))

                .take(3)
                .log();//starts from 0 -> .....

        StepVerifier.create(interval)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}
