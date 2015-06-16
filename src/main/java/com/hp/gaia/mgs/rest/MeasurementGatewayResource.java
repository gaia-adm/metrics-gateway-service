package com.hp.gaia.mgs.rest;

import com.hp.gaia.mgs.dto.Metric;
import com.hp.gaia.mgs.services.MetricsCollectorService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
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
            try {
                for (Metric metric : metrics) {
                    metricsCollector.storeMetric(metric);
                }

            } catch (IOException e) {
                WebApplicationException was = new WebApplicationException("Something went wrong!");
                response.resume(was);
            }

        }).thenApply((result) -> response.resume(Response.status(Response.Status.CREATED).entity(result).build()));


    }


}
