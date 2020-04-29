package com.learnreactivespring.fluxAndMonoTest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPubliserTest {
@Test
    public void coldPublisher() throws InterruptedException {
        Flux<String> just = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofMillis(1000));

        just.subscribe(s->System.out.println("Subscriber 1 : " +s));//emits the value from the beginning
        Thread.sleep(2000);

        just.subscribe(s->System.out.println("Subscriber 2 :" + s));//emits the value from the beginning

        Thread.sleep(8000);
}

    @Test
    public void hotPublisher() throws InterruptedException {
        Flux<String> just = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofMillis(1000));

        ConnectableFlux<String> publish = just.publish();
        publish.connect();
        publish.subscribe(s->System.out.println("Subscriber 1 " +s));//Starts from beginning.
        Thread.sleep(2000);
        publish.subscribe(s->System.out.println("Subscriber 2 " +s));//Does not start from beginning.
        Thread.sleep(5000);

    }
}
