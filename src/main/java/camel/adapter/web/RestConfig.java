package camel.adapter.web;

import camel.adapter.domain.MessageA;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.HttpStatus;
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
                .route()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NO_CONTENT.value()))
                .to("direct:filter")
                .transform().constant("");
    }
}
