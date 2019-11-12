package grails.plugin.facebooksdk

import spock.lang.Specification

class FacebookExtensionsSpec extends Specification {

    void 'test collation'() {
        when:
            List<List<String>> collate40 = FacebookExtensions.collate(['a'] * 2, 10)
        then:
            collate40.size() == 1
            collate40[0].size() == 2

        when:
            List<List<String>> collate60 = FacebookExtensions.collate(['a'] * 5, 2)
        then:
            collate60.size() == 3
            collate60[0].size() == 2
            collate60[1].size() == 2
            collate60[2].size() == 1
    }

}
