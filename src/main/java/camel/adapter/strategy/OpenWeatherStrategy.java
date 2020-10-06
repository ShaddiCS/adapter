package camel.adapter.strategy;

import camel.adapter.domain.MessageA;
import camel.adapter.domain.MessageB;
import camel.adapter.domain.OpenWeatherMapObject;
import camel.adapter.web.WeatherRoute;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;

@Profile("open_weather")
@Component
public class OpenWeatherStrategy implements WeatherStrategy {

    private static final String REST_URL = "api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${camel.weather.token}")
    private String token;

    @SneakyThrows
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        JacksonDataFormat dataFormat = new JacksonDataFormat(objectMapper, OpenWeatherMapObject.class);

        OpenWeatherMapObject body = (OpenWeatherMapObject) dataFormat.unmarshal(newExchange, newExchange.getIn().getBody(InputStream.class));

        MessageA messageA = (MessageA) oldExchange.getIn().getBody();
        MessageB messageB = new MessageB(messageA.getMsg(), LocalDateTime.now().format(WeatherRoute.dateTimeFormatter), body.getMain().getTemp().intValue());
        newExchange.getIn().setBody(messageB);

        return newExchange;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageA messageA = (MessageA) exchange.getIn().getBody();
        exchange.getIn().setHeader("url", String.format(REST_URL,
                messageA.getCoordinates().getLatitude(),
                messageA.getCoordinates().getLongitude(),
                token
        ));
    }
}
