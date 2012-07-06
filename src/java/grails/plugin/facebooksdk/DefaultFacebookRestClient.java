package grails.plugin.facebooksdk;

import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultLegacyFacebookClient;
import com.restfb.DefaultWebRequestor;

import java.net.HttpURLConnection;

public class DefaultFacebookRestClient extends DefaultLegacyFacebookClient {

    // Override default web requestor to add read timeout parameter
    DefaultFacebookRestClient(String accessToken, final Integer timeout) {
        super(accessToken,
                new DefaultWebRequestor() {
                    @Override
                    protected void customizeConnection(HttpURLConnection connection) {
                        connection.setReadTimeout(timeout);
                    }
                },
                new DefaultJsonMapper());
    }

}
