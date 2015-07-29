package com.hp.gaia.mgs.rest.filters;

import com.hp.gaia.mgs.rest.context.TenantContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by belozovs on 7/29/2015.
 * Check presence of valid tenantId
 */
public class TenantContextFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(TenantContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (TenantContextHolder.getInstance().getTenantIdLocal() == null || TenantContextHolder.getInstance().getTenantIdLocal().isEmpty()) {
            logger.error("No valid tenant provided by the request");
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.reset();
            httpResponse.setStatus(Response.Status.PRECONDITION_FAILED.getStatusCode());
            return;
        }
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
