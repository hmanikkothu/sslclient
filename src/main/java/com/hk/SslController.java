package com.hk;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.security.ssl.SSLContextImpl;

/**
 * Created by hmanikkothu on 4/3/2017.
 */
@RestController
public class SslController {

    @RequestMapping("/hello")
    String hello() {
        return "Hello";
    }

    @RequestMapping("/http")
    String google() {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "http://www.google.com";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + "", String.class);
        return response.toString();
    }

    @RequestMapping("/https")
    String googleSSL() throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "https://accounts.google.com";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + "/ServiceLogin", String.class);
        return response.toString();
    }

}
