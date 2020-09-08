package camel.adapter.domain;

import lombok.Data;

@Data
public class MessageA {
    private String msg;
    private Language lng;
    private Coordinate coordinates;

    @Data
    public static class Coordinate {
        private String latitude;
        private String longitude;
    }
}
