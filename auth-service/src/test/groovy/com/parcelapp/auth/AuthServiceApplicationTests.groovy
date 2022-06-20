package com.parcelapp.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceApplicationTests extends Specification {

    @Autowired
    private ApplicationContext applicationContext

    def "context loads"() {
        expect: "the applicationContext is created"
        applicationContext
    }
}
