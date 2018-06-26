package grails.plugin.facebooksdk

import grails.core.GrailsApplication
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean

@CompileStatic
class FacebookGraphClientService implements InitializingBean {

    String proxyHost
    Integer proxyPort
    String graphEndpoint
    String graphVideoEndpoint
    String readOnlyEndpoint

    GrailsApplication grailsApplication

    FacebookGraphClient newClient(String accessToken = '',
                                  String apiVersion = FacebookGraphClient.DEFAULT_API_VERSION,
                                  Integer timeout = FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS) {
        return new FacebookGraphClient(accessToken, apiVersion, timeout, proxyHost, proxyPort) {
            @Override
            protected String getFacebookGraphEndpointUrl() {
                if (!graphEndpoint) {
                    return super.getFacebookGraphEndpointUrl()
                }
                if (this.apiVersion.isUrlElementRequired()) {
                    return graphEndpoint + '/' + this.apiVersion.getUrlElement();
                }
                if (this.unsupportedApiVersion) {
                    return graphEndpoint + '/' + this.apiVersionString
                }
                return graphEndpoint;
            }

            @Override
            protected String getFacebookGraphVideoEndpointUrl() {
                if (!graphVideoEndpoint) {
                    return super.getFacebookGraphVideoEndpointUrl()
                }
                if (this.apiVersion.isUrlElementRequired()) {
                    return graphVideoEndpoint + '/' + this.apiVersion.getUrlElement();
                }
                if (this.unsupportedApiVersion) {
                    return graphVideoEndpoint + '/' + this.apiVersionString
                }
                return graphVideoEndpoint;
            }

            @Override
            protected String getFacebookReadOnlyEndpointUrl() {
                if (!readOnlyEndpoint) {
                    return super.getFacebookReadOnlyEndpointUrl()
                }
                if (this.apiVersion.isUrlElementRequired()) {
                    return readOnlyEndpoint + '/' + this.apiVersion.getUrlElement();
                }
                if (this.unsupportedApiVersion) {
                    return readOnlyEndpoint + '/' + this.apiVersionString
                }
                return readOnlyEndpoint;
            }
        }
    }

    @Override
    @CompileDynamic
    void afterPropertiesSet() throws Exception {
        if (grailsApplication.config.grails.plugin.facebooksdk.proxyHost) {
            proxyHost = grailsApplication.config.grails.plugin.facebooksdk.proxyHost
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.proxyPort) {
            proxyPort = grailsApplication.config.grails.plugin.facebooksdk.proxyPort
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.graphEndpoint) {
            graphEndpoint = grailsApplication.config.grails.plugin.facebooksdk.graphEndpoint
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.graphVideoEndpoint) {
            graphVideoEndpoint = grailsApplication.config.grails.plugin.facebooksdk.graphVideoEndpoint
        }
        if (grailsApplication.config.grails.plugin.facebooksdk.readOnlyEndpoint) {
            readOnlyEndpoint = grailsApplication.config.grails.plugin.facebooksdk.readOnlyEndpoint
        }
    }
}
