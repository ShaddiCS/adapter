package camel.adapter.strategy;

import camel.adapter.domain.MessageA;
import camel.adapter.domain.MessageB;
import camel.adapter.domain.OpenWeatherMapObject;
import camel.adapter.web.RestConfig;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.time.LocalDateTime;

public class OpenWeatherStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        MessageA messageA = (MessageA) oldExchange.getIn().getBody();
        OpenWeatherMapObject body = (OpenWeatherMapObject) newExchange.getIn().getBody();
        MessageB messageB = new MessageB(messageA.getMsg(), LocalDateTime.now().format(RestConfig.dateTimeFormatter), body.getMain().getTemp().intValue());
        newExchange.getOut().setBody(messageB);
        return newExchange;
    }
}
