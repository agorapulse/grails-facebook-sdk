package grails.plugin.facebooksdk

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class FacebookGraphClientSpec extends Specification {

    void 'version #version produces graph endpoint url #graphEndpointUrl'() {
        expect:
            new FacebookGraphClient('token', version).facebookGraphEndpointUrl == graphEndpointUrl

        where:
            version | graphEndpointUrl
            'v2.11' | 'https://graph.facebook.com/v2.11'
            'v2.12' | 'https://graph.facebook.com/v2.12'
            'v3.0'  | 'https://graph.facebook.com/v3.0'
    }
    void 'version #version produces graph video endpoint url #graphEndpointUrl'() {
        expect:
            new FacebookGraphClient('token', version).facebookGraphVideoEndpointUrl == graphEndpointUrl

        where:
            version | graphEndpointUrl
            'v2.11' | 'https://graph-video.facebook.com/v2.11'
            'v2.12' | 'https://graph-video.facebook.com/v2.12'
            'v3.0'  | 'https://graph-video.facebook.com/v3.0'
    }
    void 'version #version produces graph read only endpoint url #graphEndpointUrl'() {
        expect:
            new FacebookGraphClient('token', version).facebookReadOnlyEndpointUrl == graphEndpointUrl

        where:
            version | graphEndpointUrl
            'v2.11' | 'https://api-read.facebook.com/method/v2.11'
            'v2.12' | 'https://api-read.facebook.com/method/v2.12'
            'v3.0'  | 'https://api-read.facebook.com/method/v3.0'
    }

}
