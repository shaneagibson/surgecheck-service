package uk.co.epsilontechnologies.surgecheck.gateway;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class PassThroughResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException { }

}
