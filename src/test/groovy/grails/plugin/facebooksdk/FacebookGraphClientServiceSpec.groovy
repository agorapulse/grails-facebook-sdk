package grails.plugin.facebooksdk

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(FacebookGraphClientService)
class FacebookGraphClientServiceSpec extends Specification {


    private static final Integer PROXY_PORT = 55555
    private static final String PROXY_HOST = 'proxy.example.com'
    private static final String GRAPH_ENDPOINT = 'graph.example.com'
    private static final String GRAPH_VIDEO_ENDPOINT = 'video.example.com'
    private static final String READONLY_ENDPOINT = 'ro.example.com'

    static doWithConfig(c) {
        c.grails.plugin.facebooksdk.proxyPort = PROXY_PORT
        c.grails.plugin.facebooksdk.proxyHost = PROXY_HOST
        c.grails.plugin.facebooksdk.graphEndpoint = GRAPH_ENDPOINT
        c.grails.plugin.facebooksdk.graphVideoEndpoint = GRAPH_VIDEO_ENDPOINT
        c.grails.plugin.facebooksdk.readOnlyEndpoint = READONLY_ENDPOINT
    }


    void 'test configuration read'() {
        given:
            service.afterPropertiesSet()
        expect:
            service.proxyPort == PROXY_PORT
            service.proxyHost == PROXY_HOST
            service.graphEndpoint == GRAPH_ENDPOINT
            service.graphVideoEndpoint == GRAPH_VIDEO_ENDPOINT
    }

}
