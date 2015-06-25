package com.hp.gaia.mgs.spring;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

/**
 * Created by belozovs on 6/21/2015.
 */
public class MyCustomException extends RestClientException {

        private HttpStatus statusCode;

        private String body;

        public MyCustomException(String msg) {
            super(msg);
            // TODO Auto-generated constructor stub
        }

        public MyCustomException(HttpStatus statusCode, String body, String msg) {
            super(msg);
            this.statusCode = statusCode;
            this.body = body;
        }

        public HttpStatus getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

}
