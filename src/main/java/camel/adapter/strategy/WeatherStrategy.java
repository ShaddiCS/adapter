package camel.adapter.strategy;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Processor;

public interface WeatherStrategy extends AggregationStrategy, Processor {
}
