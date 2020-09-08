package camel.adapter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageB {
    private String txt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String dateTime;
    private Integer currentTemp;
}
