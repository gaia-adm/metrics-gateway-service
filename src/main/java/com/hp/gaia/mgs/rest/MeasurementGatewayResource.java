package com.hp.gaia.mgs.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import com.hp.gaia.mgs.rest.context.TenantContextHolder;
import com.hp.gaia.mgs.services.MetricsCollectorService;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by belozovs on 5/21/2015.
 * Endpoint to get the collected events of all type
 */
@Path("/v1/gateway")
@Singleton
public class MeasurementGatewayResource {

    MetricsCollectorService metricsCollector = new MetricsCollectorService();

    public MeasurementGatewayResource() throws IOException {
    }

    @POST
    @Path("/event")
    public void publishEvent(@Context HttpServletRequest request, @Suspended final AsyncResponse response, String jsonEvents) throws ExecutionException, InterruptedException, IOException {

        String tenantId = TenantContextHolder.getInstance().getTenantIdLocal();

        CompletableFuture.supplyAsync(() -> {
            try {
                List<BaseEvent> receivedEvents = new ObjectMapper().readValue(jsonEvents, new TypeReference<List<BaseEvent>>() {
                });
                System.out.println("Tenant " + tenantId + " Got result, number of points: " + receivedEvents.size());
                metricsCollector.publishEvent(receivedEvents, tenantId);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
            }
        }).handle((result, ex) -> {
            if (result == null) {
                return response.resume(Response.status(Response.Status.CREATED).build());

            } else {
                return response.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build());

            }
        });


    }


}
