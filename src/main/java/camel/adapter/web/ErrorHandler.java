package camel.adapter.web;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandler extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:service_down")
                .transform().constant("")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.REQUEST_TIMEOUT.value()));

        from("direct:handleEmpty")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }
}
