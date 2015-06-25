package com.hp.gaia.mgs.spring;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

/**
 * Created by belozovs on 6/21/2015.
 */
public class CustomResponseErrorHandler implements ResponseErrorHandler{

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        String body = IOUtils.toString(response.getBody());
        MyCustomException myCustomException = new MyCustomException(HttpStatus.INTERNAL_SERVER_ERROR, body, body);
        throw myCustomException;
    }
}
