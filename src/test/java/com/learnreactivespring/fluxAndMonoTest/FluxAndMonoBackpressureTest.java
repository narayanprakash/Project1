package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackpressureTest {



    @Test
    public void backPressureTest(){
        Flux<Integer> log = Flux.range(1, 30)
                .log();

        StepVerifier.create(log)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure_customized(){
        Flux<Integer> log = Flux.range(1, 30)
                .log();

        log.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value is : "+value);
                if(value==6){
                    cancel();
                }
            }
        });

    }
}
