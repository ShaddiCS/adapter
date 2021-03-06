package camel.adapter.web;

import camel.adapter.domain.Language;
import camel.adapter.domain.MessageA;
import camel.adapter.domain.MessageB;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.UnknownHostException;

import static camel.adapter.web.TestData.getMessageA;
import static camel.adapter.web.TestData.getMessageB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@UseAdviceWith
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class RestConfigTest {
    private static final String SERVICE_B_URI = "/rest/messageB";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private RestTemplate temp;
    private MockRestServiceServer mockServer;
    @Autowired
    private CamelContext context;

    @EndpointInject("mock:process")
    private MockEndpoint processMock;

    @EndpointInject("mock:end")
    private MockEndpoint endMock;

    @Before
    public void beforeEach() throws Exception{
        AdviceWithRouteBuilder.adviceWith(context, "sender", ex -> ex.weaveById("end").replace().to("mock:end").process(exchange -> {
            String body = exchange.getIn().getBody(String.class);
            RequestEntity<String> requestEntity = RequestEntity
                    .post(new URI(SERVICE_B_URI))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
            temp.exchange(requestEntity, String.class);
        }));
    }

    @PostConstruct
    public void postConstruct() {
        mockServer = MockRestServiceServer.createServer(temp);
    }

    @Test
    public void testOk() throws Exception {
        MessageA messageA = getMessageA();
        MessageB messageB = getMessageB(messageA);
        String messageBString = objectMapper.writeValueAsString(messageB);

        processMock.expectedMessageCount(1);
        processMock.expectedBodiesReceived(messageB);

        endMock.expectedMessageCount(1);
        endMock.expectedBodiesReceived(messageBString);

        AdviceWithRouteBuilder.adviceWith(context, "process", ex -> ex.weaveById("getWeather").replace().process(ch -> {
            MessageB mockB = getMessageB(ch.getIn().getBody(MessageA.class));
            ch.getIn().setBody(mockB);
        }).to("mock:process"));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(SERVICE_B_URI)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(messageBString))
                .andRespond(withStatus(HttpStatus.OK));

        ResponseEntity<String> response = restTemplate.postForEntity("/camel/message", messageA, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        endMock.assertIsSatisfied();
        processMock.assertIsSatisfied();
    }

    @Test
    public void testServiceDown() throws Exception {
        MessageA messageA = getMessageA();

        processMock.expectedMessageCount(0);
        endMock.expectedMessageCount(0);

        mockServer.expect(ExpectedCount.never(),
                requestTo(new URI(SERVICE_B_URI)));

        AdviceWithRouteBuilder.adviceWith(context, "process", ex -> ex.weaveById("getWeather").replace().process(ch -> {
            throw new UnknownHostException();
        }).to("mock:process"));

        ResponseEntity<String> response = restTemplate.postForEntity("/camel/message", messageA, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.REQUEST_TIMEOUT);

        processMock.assertIsSatisfied();
        endMock.assertIsSatisfied();
    }

    @Test
    public void testWrongLng() throws Exception {
        MessageA messageA = getMessageA();
        messageA.setLng(Language.EN);

        processMock.expectedMessageCount(0);
        endMock.expectedMessageCount(0);

        mockServer.expect(ExpectedCount.never(),
                requestTo(new URI(SERVICE_B_URI)));

        ResponseEntity<String> response = restTemplate.postForEntity("/camel/message", messageA, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        processMock.assertIsSatisfied();
        endMock.assertIsSatisfied();
    }

    @Test
    public void testEmptyMessage() throws Exception {
        MessageA messageA = getMessageA();
        messageA.setMsg("");

        processMock.expectedMessageCount(0);
        endMock.expectedMessageCount(0);

        mockServer.expect(ExpectedCount.never(),
                requestTo(new URI(SERVICE_B_URI)));

        ResponseEntity<String> response = restTemplate.postForEntity("/camel/message", messageA, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        processMock.assertIsSatisfied();
        endMock.assertIsSatisfied();
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public RestTemplate beanRestTemplate() {
            return new RestTemplate();
        }
    }
}