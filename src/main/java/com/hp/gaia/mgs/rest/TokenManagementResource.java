package com.hp.gaia.mgs.rest;

import com.hp.gaia.mgs.rest.oauth2.TokensDB;
import com.hp.gaia.mgs.services.MonitoringService;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Response;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by belozovs on 5/21/2015.
 */

@Path("/auth")
public class TokenManagementResource {

    private final static Logger logger = LoggerFactory.getLogger(TokenManagementResource.class);

    private ScheduledExecutorService scheduler;

    @POST
    @Path("/set/{tenantId}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response monitoringControl(@PathParam("tenantId") String tenantId) {
        if (tenantId != null) {
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            try {
                String token = oauthIssuerImpl.accessToken();
                TokensDB.tokensPerTenant.put(token, tenantId);
                return Response.status(Response.Status.OK).entity(token).build();
            } catch (OAuthSystemException e) {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate token").build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/get/{tenantId}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getTenantTokens(@PathParam("tenantId") String tenantId) {
        List<String> result = new ArrayList<>();
        if (tenantId != null) {

            Iterator<String> keys = TokensDB.tokensPerTenant.keySet().iterator();
            while (keys.hasNext()) {
                String token = keys.next();
                if (TokensDB.tokensPerTenant.get(token).equals(tenantId)) {
                    result.add(token);
                }
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }
}
