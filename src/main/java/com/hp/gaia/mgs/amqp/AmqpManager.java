package com.hp.gaia.mgs.amqp;

import com.hp.gaia.mgs.services.PropertiesKeeperService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Created by belozovs on 6/30/2015.
 */

public class AmqpManager {

    private static String MQ_SERVER_NAME, MQ_SERVER_USERNAME, MQ_SERVER_PASSWORD, QUEUE_NAME;
    private static Integer MQ_SERVER_PORT;

    private Channel channel = null;

    public AmqpManager() throws IOException {
        Properties properties = PropertiesKeeperService.getInstance().getProperties();
        MQ_SERVER_NAME = properties.getProperty("amqpHost");
        MQ_SERVER_PORT = Integer.parseInt(properties.getProperty("amqpPort"));
        MQ_SERVER_USERNAME = properties.getProperty("amqpUser");
        MQ_SERVER_PASSWORD = properties.getProperty("amqpPassword");
        QUEUE_NAME = properties.getProperty("amqpRoutingKey");

        System.out.println("MQ details: " + MQ_SERVER_NAME + ":" + MQ_SERVER_PORT + ":" + QUEUE_NAME);

    }

    public Channel getChannel() throws IOException, TimeoutException {
        if (channel == null) {
            ConnectionFactory factory = createConnectionFactory();
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null); //durable, non-exclusive, no auto-delete
        }
        return channel;
    }

    public String getQueueName() {
        return QUEUE_NAME;
    }

    private ConnectionFactory createConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_SERVER_NAME);
        factory.setPort(MQ_SERVER_PORT);
        factory.setUsername(MQ_SERVER_USERNAME);
        factory.setPassword(MQ_SERVER_PASSWORD);

        factory.setAutomaticRecoveryEnabled(true); // connection that will recover automatically
        factory.setNetworkRecoveryInterval(10000); // attempt recovery to the max of 10 seconds
        factory.setRequestedHeartbeat(30); // Setting heartbeat to 30 sec instead the default of 10 min

        return factory;
    }


}
