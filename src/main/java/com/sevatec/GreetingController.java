package com.sevatec;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello from %s!";
    private final AtomicLong counter = new AtomicLong();
    
    @RequestMapping("/")
    public Greeting greetingDefault() {
        return new Greeting(0,"Yolo");
    }

    @RequestMapping("/hello")
    public Greeting hello() {
        return new Greeting(0,"Hello");
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="Sevatec") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
}
