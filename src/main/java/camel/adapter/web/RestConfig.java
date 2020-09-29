package camel.adapter.web;

import camel.adapter.domain.MessageA;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestConfig extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .port("{{server.port}}")
                .contextPath("camel")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/message").id("message")
                .post()
                .consumes("application/json")
                .type(MessageA.class)
                .bindingMode(RestBindingMode.json)
                .to("direct:filter");
    }
}
