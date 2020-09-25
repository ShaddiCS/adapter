package camel.adapter.web;

import camel.adapter.domain.MessageB;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/rest/messageB")
    public void post(@RequestBody MessageB messageB) {
        System.out.println("Controller passed: " + messageB);
    }
}
