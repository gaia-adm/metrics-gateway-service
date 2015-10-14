package com.hp.gaia.mgs.rest;

import com.hp.gaia.mgs.services.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import org.springframework.util.StringUtils;


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

    /**
     * Partial or temporary solution for dynamically changing root log level
     * This operation introduces additional security breach as well as other operations in this resource
     * It should be either replaced with different approach (preferably adjusted with other java-based services)
     * or protected with special (administrator/tenant 0) token or user name
     * @param level one of following case-insensitive values: trace, debug, info, warn, error (default level)
     * @return string with the level that was actually set
     */
    @GET
    @Path("/log/{level}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setLogLevel(@PathParam("level") String level){

        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) root;
        rootLogger.setLevel(getLogLevel(level));

        return Response.status(Response.Status.OK).entity("Log level set to " + rootLogger.getLevel().toString()).build();
    }

    private Level getLogLevel(String levelString){

        Level level;

        if(StringUtils.isEmpty(levelString)){
            levelString="";
        }

        switch (levelString.toLowerCase()){
            case "trace":
                level=Level.TRACE;
                break;
            case "debug":
                level=Level.DEBUG;
                break;
            case "info":
                level=Level.INFO;
                break;
            case "warning":
                level=Level.WARN;
                break;
            case "eror":
                level=Level.ERROR;
                break;
            default:
                level=Level.ERROR;
                break;
        }

        return level;
    }


}
