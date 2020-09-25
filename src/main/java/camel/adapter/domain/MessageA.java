package camel.adapter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageA {
    private String msg;
    private Language lng;
    private Coordinate coordinates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinate {
        private String latitude;
        private String longitude;
    }
}
