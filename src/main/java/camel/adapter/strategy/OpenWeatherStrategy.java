package camel.adapter.strategy;

import camel.adapter.domain.MessageA;
import camel.adapter.domain.MessageB;
import camel.adapter.domain.OpenWeatherMapObject;
import camel.adapter.web.RestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;

@Profile("open_weather")
@Component
public class OpenWeatherStrategy implements WeatherStrategy {

    @Autowired
    ObjectMapper objectMapper;


    @SneakyThrows
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        JacksonDataFormat dataFormat = new JacksonDataFormat(objectMapper, OpenWeatherMapObject.class);

        OpenWeatherMapObject body = (OpenWeatherMapObject) dataFormat.unmarshal(newExchange, newExchange.getIn().getBody(InputStream.class));

        MessageA messageA = (MessageA) oldExchange.getIn().getBody();
        MessageB messageB = new MessageB(messageA.getMsg(), LocalDateTime.now().format(RestConfig.dateTimeFormatter), body.getMain().getTemp().intValue());
        newExchange.getOut().setBody(messageB);
        return newExchange;
    }

    @Override
    public String getUrl(String longitude, String latitude, String... params) {
        String paramString = String.join("&", params);

        return String.format("api.openweathermap.org/data/2.5/weather"
                + "?lat=%s&lon=$%s%s", longitude, latitude, paramString.isEmpty() ? "" : "&" + paramString);
    }
}
