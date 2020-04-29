package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "Jack", "Jenny");

    @Test
    public void tranformUsingMap() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .filter(n -> n.length() > 4)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("JENNY")
                .verifyComplete();

    }

    @Test
    public void tranformUsingMap_Length() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(String::length)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();

        Flux<Integer> stringFlux2 = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(stringFlux2)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformFlatMap() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return Flux.fromIterable(Arrays.asList(s, "new Value"));//A->List[A,"newValue"]
                }).log();//Db or external service call that returns a flux s->Flux<String>.
        StepVerifier.create(stringFlux)
                .expectNextCount(12).verifyComplete();

    }

    @Test
    public void transformFlatMap_parallel() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))//Flux<String>
                .window(2)//Flux<Flux<String>->(A,B),(C,D),(E,F)>
                .flatMap((s) -> s.map(this::convertToList).subscribeOn(parallel()))     //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s))//Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12).verifyComplete();


//This Maintain the order but is not faster
        Flux<String> stringFlux1 = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))//Flux<String>
                .window(2)//Flux<Flux<String>->(A,B),(C,D),(E,F)>
                .concatMap((s) -> s.map(this::convertToList).subscribeOn(parallel()))     //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s))//Flux<String>
                .log();

        StepVerifier.create(stringFlux1)
                .expectNextCount(12).verifyComplete();

        //Maintains the order and faster
        Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))//Flux<String>
                .window(2)//Flux<Flux<String>->(A,B),(C,D),(E,F)>
                .flatMapSequential((s) -> s.map(this::convertToList).subscribeOn(parallel()))     //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s))//Flux<String>
                .log();

        StepVerifier.create(stringFlux1)
                .expectNextCount(12).verifyComplete();

    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "new Value");
    }


}
