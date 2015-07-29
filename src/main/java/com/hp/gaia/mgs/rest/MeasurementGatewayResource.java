package com.hp.gaia.mgs.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import com.hp.gaia.mgs.dto.OldMetric;
import com.hp.gaia.mgs.rest.context.TenantContextHolder;
import com.hp.gaia.mgs.services.MetricsCollectorService;
import com.hp.gaia.mgs.services.PropertiesKeeperService;
import com.hp.gaia.mgs.spring.MultiTenantOAuth2Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by belozovs on 5/21/2015.
 * Endpoint to get the collected events of all type
 */
@Path("/v1/gateway")
public class MeasurementGatewayResource {

    MetricsCollectorService metricsCollector = new MetricsCollectorService();


    private Boolean useAmqp = Boolean.valueOf(PropertiesKeeperService.getInstance().getEnvOrPropAsString("useAmqp"));

    public MeasurementGatewayResource() throws IOException {
        System.out.println("useAmqp flag is " + useAmqp);
        if (useAmqp) {
            System.out.println("Metrics will be published to RabbitMQ");
        } else {
            System.out.println("Metrics will be published to log file");
        }

    }

    @POST
    @Path("/publish")
    @Consumes("application/json")
    @Produces("application/json")
    public void publishMetricAsync(@Context HttpServletRequest request, @Suspended final AsyncResponse response, List<OldMetric> metrics) {


        CompletableFuture.runAsync(() -> {
            for (OldMetric metric : metrics) {
                metricsCollector.storeMetric(metric);
            }
        }).thenApply((result) -> response.resume(Response.status(Response.Status.CREATED).entity(result).build()));
    }


    @POST
    @Path("/publish2")
    @Consumes("application/json")
    @Produces("application/json")
    public void publishMetricAsync2(@Context HttpServletRequest request, @Suspended final AsyncResponse response, JsonNode jsonMetrics) throws ExecutionException, InterruptedException {

        Map tenantDetails = ((MultiTenantOAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getTenantDetails();

        System.out.println("metric received");

        CompletableFuture.supplyAsync(() -> {
            if (useAmqp) {
                try {
                    metricsCollector.publishMetric(String.valueOf(jsonMetrics.get("points")), String.valueOf(tenantDetails.get("tenantId")));
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
                }
            } else {
                metricsCollector.storeMetric(String.valueOf(jsonMetrics.get("points").size()), (Integer) tenantDetails.get("tenantId"));
                return null;
            }
        }).handle((result, ex) -> {
            if (result == null) {
                return response.resume(Response.status(Response.Status.CREATED).entity(result).build());

            } else {
                return response.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build());

            }
        });

    }

    @POST
    @Path("/publish3")
    public void publishMetricAsync3(@Context HttpServletRequest request, @Suspended final AsyncResponse response, String jsonMetrics) throws ExecutionException, InterruptedException {

        Map tenantDetails = ((MultiTenantOAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getTenantDetails();

        System.out.println("metric received");

        CompletableFuture.supplyAsync(() -> {
            if (useAmqp) {
                try {
                    metricsCollector.publishMetric(jsonMetrics, String.valueOf(tenantDetails.get("tenantId")));
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
                }
            } else {
                metricsCollector.storeMetric(jsonMetrics, (Integer) tenantDetails.get("tenantId"));
                return null;
            }
        }).handle((result, ex) -> {
            if (result == null) {
                return response.resume(Response.status(Response.Status.CREATED).build());

            } else {
                return response.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build());

            }
        });

    }

    @POST
    @Path("/event")
    public void publishEvent(@Context HttpServletRequest request, @Suspended final AsyncResponse response, String jsonEvents) throws ExecutionException, InterruptedException, IOException {

        String tenantId = TenantContextHolder.getInstance().getTenantIdLocal();
        TenantContextHolder.getInstance().setTenantIdLocal(null);


        CompletableFuture.supplyAsync(() -> {
            if (useAmqp) {
                try {
                    List<BaseEvent> receivedEvents = new ObjectMapper().readValue(jsonEvents, new TypeReference<List<BaseEvent>>() {});
                    System.out.println("Tenant " + tenantId + " Got result, number of points: " + receivedEvents.size());
                    metricsCollector.publishEvent(receivedEvents, tenantId);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
                }
            } else {
                try {
                    List<BaseEvent> receivedEvents = new ObjectMapper().readValue(jsonEvents, new TypeReference<List<BaseEvent>>() {});
                    System.out.println("Tenant " + tenantId + " Got result, number of points: " + receivedEvents.size());
                    metricsCollector.storeEvent(receivedEvents, tenantId);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
                }
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
