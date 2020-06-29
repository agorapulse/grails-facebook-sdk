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
            'v3.0' | 'https://graph.facebook.com/v3.0'
            'v4.0' | 'https://graph.facebook.com/v4.0'
            'v5.0'  | 'https://graph.facebook.com/v5.0'
    }
    
    void 'version #version produces graph video endpoint url #graphEndpointUrl'() {
        expect:
            new FacebookGraphClient('token', version).facebookGraphVideoEndpointUrl == graphEndpointUrl

        where:
            version | graphEndpointUrl
            'v3.0' | 'https://graph-video.facebook.com/v3.0'
            'v4.0' | 'https://graph-video.facebook.com/v4.0'
            'v5.0'  | 'https://graph-video.facebook.com/v5.0'
    }

}
