package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names= Arrays.asList("adam","anna","Jack","Jenny");

    @Test
    public void filterTest(){

        Flux<String> namesFlux=Flux.fromIterable(names)
                .filter(s->s.endsWith("m"))
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("adam")
                .verifyComplete();

        Flux<String> log = Flux.fromIterable(names)
                .filter(s -> s.length() == 4).log();

        StepVerifier.create(log)
                .expectNext("adam","anna","Jack")
                .verifyComplete();

    }
}
