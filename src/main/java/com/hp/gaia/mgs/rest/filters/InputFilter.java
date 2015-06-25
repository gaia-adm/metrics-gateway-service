package com.hp.gaia.mgs.rest.filters;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by belozovs on 5/30/2015.
 */

public class InputFilter implements Filter {

//    private final static Logger logger = LoggerFactory.getLogger(InputFilter.class);

    private final String API_KEY_HEADER = "api_key";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest.getContentLengthLong() < 2) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.reset();
            response.setStatus(Response.Status.PRECONDITION_FAILED.getStatusCode());
            return;
        }
        //printing double the response time and increases a number of threads so
//        logger.debug("api_key header: {}", ((HttpServletRequest) servletRequest).getHeader(API_KEY_HEADER));
//        logger.debug("Request length: {}", servletRequest.getContentLengthLong());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}