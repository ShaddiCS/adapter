package camel.adapter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapObject{
    private WeatherMain main;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherMain {
        private Double temp;
    }
}
