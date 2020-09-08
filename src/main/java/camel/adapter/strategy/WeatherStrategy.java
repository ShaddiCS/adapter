package camel.adapter.strategy;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class WeatherStrategy implements AggregationStrategy{
    private AggregationStrategy weatherStrategy;

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        return weatherStrategy.aggregate(oldExchange, newExchange);
    }

    public void setWeatherStrategy(AggregationStrategy weatherStrategy) {
        this.weatherStrategy = weatherStrategy;
    }

    public static WeatherStrategy of(AggregationStrategy weatherStrategy) {
        WeatherStrategy strategy = new WeatherStrategy();
        strategy.setWeatherStrategy(weatherStrategy);
        return strategy;
    }
}
