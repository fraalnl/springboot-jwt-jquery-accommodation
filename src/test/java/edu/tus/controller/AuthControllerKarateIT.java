package edu.tus.controller;

import org.springframework.boot.test.context.SpringBootTest;
import com.intuit.karate.junit5.Karate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthControllerKarateIT {
    @Karate.Test
    Karate testAuth() {
        return Karate.run("auth").relativeTo(getClass());
    }
    
    //@Karate.Test
    Karate testAdmin() {
        return Karate.run("admin").relativeTo(getClass());
    }
}
