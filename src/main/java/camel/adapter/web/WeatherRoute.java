package camel.adapter.web;

import camel.adapter.strategy.WeatherStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherRoute extends RouteBuilder {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @Autowired
    private WeatherStrategy weatherStrategy;

    @Override
    public void configure() throws Exception {
        onException(UnknownHostException.class)
                .handled(true)
                .to("direct:service_down");

        from("direct:filter").routeId("filter")
                .filter(simple("${body.lng} == 'RU'"))
                .choice()
                .when(simple("${body.msg} == ''"))
                .to("direct:handleEmpty")
                .otherwise()
                .to("direct:process")
                .endChoice();

        from("direct:process").routeId("process")
                .process(weatherStrategy).id("addUrl")
                .enrich()
                .simple("http:${headers.url}" +
                        "&bridgeEndpoint=true&httpMethod=get")
                .aggregationStrategy(weatherStrategy).id("getWeather")
                .to("direct:producer");

        from("direct:producer").routeId("producer")
                .log("sending to {{camel.endpoint.target}} body=${body}")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http:{{camel.endpoint.target}}?bridgeEndpoint=true&httpMethod=post").id("end")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
