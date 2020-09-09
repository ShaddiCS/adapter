package camel.adapter.strategy;

import org.apache.camel.AggregationStrategy;

public interface WeatherStrategy extends AggregationStrategy{
    String getUrl(String longitude, String latitude, String...params);
}
