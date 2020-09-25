package camel.adapter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageB {
    private String txt;
    private String dateTime;
    private Integer currentTemp;
}
