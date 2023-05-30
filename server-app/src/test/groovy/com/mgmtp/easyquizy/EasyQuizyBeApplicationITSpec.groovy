package com.mgmtp.easyquizy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles(profiles = ['dev'])
@SpringBootTest(classes = EasyQuizyBeApplication.class)
class EasyQuizyBeApplicationITSpec extends Specification {

    @Autowired(required = false)
    ApplicationContext ctx

    def 'test application startup'() {
        expect:
            ctx
    }
}
