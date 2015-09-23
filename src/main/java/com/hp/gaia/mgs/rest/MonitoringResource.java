package com.hp.gaia.mgs.rest;

import com.hp.gaia.mgs.services.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by belozovs on 5/21/2015.
 */

@Path("/monitor")
@Singleton
public class MonitoringResource {

    private final static Logger logger = LoggerFactory.getLogger(MonitoringResource.class);

    private ScheduledExecutorService scheduler;

    @GET
    @Path("/action/{action}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response monitoringControl(@PathParam("action") String action) {

        if (scheduler == null && action.equalsIgnoreCase("on")) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(MonitoringService.getIntstance(), 0L, 1000L, TimeUnit.MILLISECONDS);
            logger.debug("Start monitoring");
            System.out.println("Start monitoring");
            return Response.status(Response.Status.OK).entity("Monitoring started").build();
        }

        if (scheduler != null && action.equalsIgnoreCase("off")) {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
                scheduler = null;
                logger.debug("Stop monitoring");
                System.out.println("Stop monitoring");
            }
            return Response.status(Response.Status.OK).entity("Monitoring stopped").build();
        }

        return Response.status(Response.Status.OK).entity("Nothing to do with monitoring").build();

    }

    @GET
    @Path("/dump")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getThreadDump(@PathParam("action") String action) {

        StringBuilder sb = new StringBuilder();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds());
        for(ThreadInfo threadInfo : threadInfos){
            sb.append(threadInfo.getThreadName());
            sb.append("\nstate: " + threadInfo.getThreadState());
            StackTraceElement[] traceElements = threadInfo.getStackTrace();
            for(int i =0; i<((traceElements.length>10) ? 10 : traceElements.length); i++){
                sb.append("\n at ").append(traceElements[i]);
            }
            sb.append("\n\n");
        }

        return Response.status(Response.Status.OK).entity(sb.toString()).build();
    }

}
