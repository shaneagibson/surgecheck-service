package uk.co.epsilontechnologies.surgecheck.gateway.uber;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.Date;

@Component
public class UberGatewayImpl implements UberGateway {

    private final RestTemplate restTemplate;
    private final String uberBaseUrl;
    private final String[] uberServerTokens;

    private int currentServerTokenIndex = 0;

    @Autowired
    public UberGatewayImpl(
            final RestTemplate restTemplate,
            @Value("${uber.base.url}") final String uberBaseUrl,
            @Value("${uber.server.tokens}") final String uberServerTokens) {
        this.restTemplate = restTemplate;
        this.uberBaseUrl = uberBaseUrl;
        this.uberServerTokens = uberServerTokens.split(",");
    }

    @Override
    public SurgeStatus getSurgeStatus(final Coordinates coordinates) {
        final Date now = new Date();
        final BigDecimal surgeMultiplier = resolveSurgeMultiplier(issuePriceEstimate(true, coordinates));
        return new SurgeStatus(now, coordinates, surgeMultiplier);
    }

    private BigDecimal resolveSurgeMultiplier(final ResponseEntity<String> response) {
        try {
            final String responseBodyString = response.getBody();
            final JSONObject responseJson = new JSONObject(responseBodyString);
            final JSONArray pricesJson = responseJson.getJSONArray("prices");
            for (int i = 0; i < pricesJson.length(); i++) {
                final JSONObject priceJson = (JSONObject) pricesJson.get(i);
                final String product = (String) priceJson.get("display_name");
                if (product != null && product.equals("uberX")) {
                    return new BigDecimal((Double) priceJson.get("surge_multiplier")).setScale(2, RoundingMode.HALF_UP);
                }
            }
            return BigDecimal.ONE;
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<String> issuePriceEstimate(final boolean retryOnThrottle, final Coordinates coordinates) {

        final URI uri = URI.create(uberBaseUrl+"/v1/estimates/price?start_latitude="+coordinates.getLatitude()+"&start_longitude="+coordinates.getLongitude()+"&end_latitude="+coordinates.getLatitude().add(new BigDecimal(0.1))+"&end_longitude="+coordinates.getLongitude());

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Token "+this.uberServerTokens[this.currentServerTokenIndex]);

        final ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            if (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS) && retryOnThrottle) {
                this.currentServerTokenIndex = this.currentServerTokenIndex == this.uberServerTokens.length - 1 ? 0 : this.currentServerTokenIndex + 1;
                return issuePriceEstimate(false, coordinates);
            }
            throw new RuntimeException("Invalid response from uber - status:"+response.getStatusCode()+", body:"+response.getBody());
        }

        return response;
    }

}