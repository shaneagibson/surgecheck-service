package uk.co.epsilontechnologies.surgecheck.util;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

public final class HttpUtil {

    private HttpUtil() {
        super();
    }

    public static String encodeAuthorization(final String username, final String password) {
        final String auth = username + ":" + password;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        return "Basic " + new String(encodedAuth);
    }

}