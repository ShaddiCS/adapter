package camel.adapter.web;

import camel.adapter.domain.MessageA;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class RestConfig extends RouteBuilder {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @Autowired
    private AggregationStrategy weatherStrategy;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .port("8080")
                .contextPath("camel")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/message")
                .post()
                .consumes("application/json")
                .type(MessageA.class)
                .bindingMode(RestBindingMode.json)
                .to("direct:processMessage");

        from("direct:processMessage")
                .enrich()
                .simple("http:api.openweathermap.org/data/2.5/weather"
                + "?lat=${body.coordinates.latitude}&lon=${body.coordinates.longitude}&appid=b14a0dc6571020d2723b5c4fa468b8ad"
                + "&bridgeEndpoint=true&httpMethod=get")
                .aggregationStrategy(weatherStrategy)
                .log("${body}");
    }
}
