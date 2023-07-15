package com.mgmtp.easyquizy.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RestClient {
    private HttpHeaders headers;
    private HttpEntity<?> httpEntity;
    private String url;
    private HttpMethod httpMethod;

    public RestClient() {
    }

    public RestClient defaultHeader() {
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        return this;
    }

    public RestClient setOptionalHeader(HttpHeaders headers){
        this.headers = headers;
        return this;
    }

    public RestClient setBearerToken(String token) {
        this.headers.set("Authorization", "Bearer " + token);
        return this;
    }

    public <T> RestClient setRequestBody(T requestBody) {
        this.httpEntity = new HttpEntity<>(requestBody, this.headers);
        return this;
    }

    public RestClient setUrl(String url) {
        this.url = url;
        return this;
    }

    public RestClient setMethod(HttpMethod httpMethod) {
        if(httpMethod == HttpMethod.GET) {
            this.httpEntity = new HttpEntity<>(this.headers);
        }
        this.httpMethod = httpMethod;
        return this;
    }

    public <T> ResponseEntity<T> call(Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                this.url,
                this.httpMethod,
                this.httpEntity,
                clazz
        );
    }
}
