package grails.plugin.facebooksdk

import com.restfb.DefaultFacebookClient
import com.restfb.DefaultJsonMapper
import com.restfb.DefaultWebRequestor
import com.restfb.FacebookClient
import com.restfb.FacebookEndpoints
import com.restfb.Version
import com.restfb.WebRequestor
import grails.core.GrailsApplication
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean

@CompileStatic
class FacebookGraphClientService implements InitializingBean, FacebookEndpoints{

    public static final int DEFAULT_READ_TIMEOUT_IN_MS = 180000

    String proxyHost
    Integer proxyPort

    String facebookEndpoint = Endpoint.SERVER.url
    String graphEndpoint = Endpoint.GRAPH.url
    String graphVideoEndpoint = Endpoint.GRAPH_VIDEO.url

    GrailsApplication grailsApplication

    FacebookClient newClient(String accessToken = '',
                             String apiVersion = Version.LATEST.urlElement,
                             Integer timeout = DEFAULT_READ_TIMEOUT_IN_MS,
                             String appSecret = null) {
        WebRequestor requestor = new DefaultWebRequestor() {
            @Override
            protected void customizeConnection(HttpURLConnection connection) {
                connection.setReadTimeout(timeout);
            }

            @Override
            protected HttpURLConnection openConnection(URL url) throws IOException {
                if (proxyHost != null && proxyPort != null) {
                    InetSocketAddress proxyLocation = new InetSocketAddress(proxyHost, proxyPort);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyLocation);
                    return (HttpURLConnection) url.openConnection(proxy);
                } else {
                    return (HttpURLConnection) url.openConnection();
                }
            }
        }
        FacebookClient client = new DefaultFacebookClient(accessToken, appSecret, requestor, new DefaultJsonMapper(), Version.getVersionFromString(apiVersion))
        client.setFacebookEndpointUrls(this)
        return client
    }

    @Override
    @CompileDynamic
    void afterPropertiesSet() throws Exception {
        Object config = grailsApplication.config.grails?.plugin?.facebooksdk

        if (!config) {
            return
        }

        if (grailsApplication.config.grails.plugin.facebooksdk.proxyHost) {
            proxyHost = config.proxyHost
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.proxyPort) {
            proxyPort = config.proxyPort
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.facebookEndpoint) {
            facebookEndpoint = config.facebookEndpoint
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.graphEndpoint) {
            graphEndpoint = config.graphEndpoint
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.graphVideoEndpoint) {
            graphVideoEndpoint = config.graphVideoEndpoint
        }
    }
}
