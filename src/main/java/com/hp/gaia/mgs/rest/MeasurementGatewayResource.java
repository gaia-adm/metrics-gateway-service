package com.hp.gaia.mgs.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.hp.gaia.mgs.dto.Metric;
import com.hp.gaia.mgs.services.MetricsCollectorService;
import com.hp.gaia.mgs.spring.MultiTenantOAuth2Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by belozovs on 5/21/2015.
 */
@Component
@Path("/v1/gateway")
public class MeasurementGatewayResource {

    MetricsCollectorService metricsCollector = new MetricsCollectorService();

    @POST
    @Path("/publish")
    @Consumes("application/json")
    @Produces("application/json")
    public void publishMetricAsync(@Context HttpServletRequest request, @Suspended final AsyncResponse response, List<Metric> metrics) {


        CompletableFuture.runAsync(() -> {
            for (Metric metric : metrics) {
                metricsCollector.storeMetric(metric);
            }
        }).thenApply((result) -> response.resume(Response.status(Response.Status.CREATED).entity(result).build()));
    }


    @POST
    @Path("/publish2")
    @Consumes("application/json")
    @Produces("application/json")
    public void publishMetricAsync2(@Context HttpServletRequest request, @Suspended final AsyncResponse response, JsonNode jsonMetrics) {

        Map tenantDetails = ((MultiTenantOAuth2Authentication)SecurityContextHolder.getContext().getAuthentication()).getTenantDetails();

        System.out.println();

        CompletableFuture.runAsync(() -> {
            metricsCollector.storeMetric(String.valueOf(jsonMetrics.get("points").size()), (Integer) tenantDetails.get("tenantId"));
        }).thenApply((result) -> response.resume(Response.status(Response.Status.CREATED).entity(result).build()));
    }

}
