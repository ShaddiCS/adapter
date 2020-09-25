package camel.adapter.web;

import camel.adapter.domain.Language;
import camel.adapter.domain.MessageA;
import camel.adapter.domain.MessageB;

import java.time.LocalDateTime;

public class TestData {
    public static final LocalDateTime MOCK_DATE = LocalDateTime.of(2020, 9, 23, 16, 37, 35);

    public static MessageB getMessageB(MessageA messageA) {
        return new MessageB(messageA.getMsg(),
                MOCK_DATE.format(RestConfig.dateTimeFormatter),
                270);
    }

    public static MessageA getMessageA() {
        return new MessageA("Test Message", Language.RU, new MessageA.Coordinate("53.2", "52.3"));
    }
}
