package camel.adapter.web;

import camel.adapter.domain.MessageA;
import camel.adapter.domain.OpenWeatherMapObject;
import camel.adapter.strategy.OpenWeatherStrategy;
import camel.adapter.strategy.WeatherStrategy;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class RestConfig extends RouteBuilder {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private WeatherStrategy weatherStrategy = WeatherStrategy.of(new OpenWeatherStrategy());

    @Override
    public void configure() throws Exception {
        CamelContext context = new DefaultCamelContext();

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
                .enrich("direct:weatherData", weatherStrategy)
                .log("${body}");

        from("direct:weatherData")
                .to("http://api.openweathermap.org/data/2.5/weather"
                        + "?lat=54.35&lon=52.52&appid=b14a0dc6571020d2723b5c4fa468b8ad"
                        + "&bridgeEndpoint=true&httpMethod=get")
                .unmarshal().json(JsonLibrary.Jackson, OpenWeatherMapObject.class);
    }
}
