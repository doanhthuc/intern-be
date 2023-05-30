package com.mgmtp.easyquizy.controllers

import com.mgmtp.easyquizy.controller.HelloWordController
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class HelloWorkControllerSpec extends Specification{

    @Subject
    def controller = new HelloWordController()

    def 'getName: return name correctly'() {
        when:
            def response = controller.getName('Test')
        then:
            response.statusCode == HttpStatus.OK
            response.body.toString() == 'Test'
    }
}
