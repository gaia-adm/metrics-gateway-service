package com.hp.gaia.mgs.rest.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by belozovs on 5/30/2015.
 * Filter out requests that contain to few data to be correct (less than 100 bytes) and requests that seem to be suspiciously large (100KB)
 */

public class InputFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(InputFilter.class);

    private final static int MINIMAL_REQUEST_SIZE=10;
    private final static int MAXIMAL_REQUEST_SIZE=102400;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest.getContentLengthLong() < MINIMAL_REQUEST_SIZE || servletRequest.getContentLengthLong() > MAXIMAL_REQUEST_SIZE) {
            logger.error("Response content is too short or too large. Actual size in bytes is {}", servletRequest.getContentLengthLong());
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.reset();
            response.setStatus(Response.Status.PRECONDITION_FAILED.getStatusCode());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}