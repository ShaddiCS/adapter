package camel.adapter.web;

import camel.adapter.domain.MessageA;
import camel.adapter.strategy.WeatherStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;

@Component
public class WeatherRoute extends RouteBuilder {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @Autowired
    private WeatherStrategy weatherStrategy;

    @Override
    public void configure() throws Exception {
        from("direct:filter").id("filter")
                .choice()
                .when(ex -> {
                    MessageA messageA = (MessageA) ex.getIn().getBody();
                    return StringUtils.isEmpty(messageA.getMsg());
                })
                .to("direct:handleEmpty")
                .otherwise()
                .to("direct:process")
                .endChoice();

        from("direct:handleEmpty")
                .process(ex -> {
                    ex.getOut().setBody("msg should not be empty");
                    ex.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
                });

        from("direct:process").id("process")
                .process(weatherStrategy).id("addUrl")
                .enrich()
                .simple("http:${headers.url}" +
                        "&bridgeEndpoint=true&httpMethod=get")
                .aggregationStrategy(weatherStrategy).id("getWeather")
                .to("direct:producer");

        from("direct:producer").id("producer")
                .log("sending to {{camel.endpoint.target}} body=${body}")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http:{{camel.endpoint.target}}?bridgeEndpoint=true&httpMethod=post").id("end")
                .transform().constant("Done!");
    }
}
