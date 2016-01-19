package com.hp.gaia.mgs.amqp;

import com.hp.gaia.mgs.services.PropertiesKeeperService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by belozovs on 6/30/2015.
 * Manage RabbitMQ connection for publish metrics
 */

public class AmqpManager {

    private static String MQ_SERVER_NAME, MQ_SERVER_USERNAME, MQ_SERVER_PASSWORD, INFLUXDB_QUEUE_NAME, MQ_EXCHANGE_NAME;
    private static Integer MQ_SERVER_PORT;

    private Channel influxDbChannel = null;
    private Channel esChannel = null;


    public AmqpManager() throws IOException {
        MQ_SERVER_NAME = PropertiesKeeperService.getInstance().getEnvOrPropAsString("amqpHost");
        MQ_SERVER_PORT = Integer.parseInt(PropertiesKeeperService.getInstance().getEnvOrPropAsString("amqpPort"));
        MQ_SERVER_USERNAME = PropertiesKeeperService.getInstance().getEnvOrPropAsString("amqpUser");
        MQ_SERVER_PASSWORD = PropertiesKeeperService.getInstance().getEnvOrPropAsString("amqpPassword");
        INFLUXDB_QUEUE_NAME = PropertiesKeeperService.getInstance().getEnvOrPropAsString("amqpInfluxdbRoutingKey");
        MQ_EXCHANGE_NAME = PropertiesKeeperService.getInstance().getEnvOrPropAsString("amqpExchangeName");

        System.out.println("MQ details: " + MQ_SERVER_NAME + ":" + MQ_SERVER_PORT + ":" +
                INFLUXDB_QUEUE_NAME + "(influxdb queue):" + MQ_EXCHANGE_NAME + "(mgs topic exchange)");

    }

    public Channel getInfluxDbChannel() throws IOException, TimeoutException {
        if (influxDbChannel == null) {
            ConnectionFactory factory = createConnectionFactory();
            Connection connection = factory.newConnection();
            influxDbChannel = connection.createChannel();
            influxDbChannel.queueDeclare(INFLUXDB_QUEUE_NAME, true, false, false, null); //durable, non-exclusive, no auto-delete
        }
        return influxDbChannel;
    }

    public String getInfluxDbQueueName() {
        return INFLUXDB_QUEUE_NAME;
    }

    public Channel getEsChannel() throws IOException, TimeoutException {
        if (esChannel == null) {
            ConnectionFactory factory = createConnectionFactory();
            Connection connection = factory.newConnection();
            esChannel = connection.createChannel();
            esChannel.exchangeDeclare(MQ_EXCHANGE_NAME, "topic", true);
        }
        return esChannel;
    }

    public String getExchangeName() { return MQ_EXCHANGE_NAME; }

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
