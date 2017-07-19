package com.m12u.amqp.consumer;

import com.m12u.amqp.listener.AMQPConsumerErrorListener;
import com.m12u.amqp.listener.AMQPConsumerErrorListenerInterface;
import com.m12u.amqp.listener.AMQPMessageListener;
import com.m12u.amqp.listener.AMQPMessageListenerInterface;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class Consumer extends Thread {

    private String user;
    private String password;
    private String host;
    private String queue;
    private String uri;
    private String vhost;
    private boolean ssl = false;
    private int port;

    private final Collection<AMQPMessageListenerInterface> amqpMessageListenerCollection = new ArrayList<AMQPMessageListenerInterface>();
    private final Collection<AMQPConsumerErrorListenerInterface> amqpConsumerErrorListener = new ArrayList<AMQPConsumerErrorListenerInterface>();

    private ConnectionFactory factory;
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channel;


    /**
     *
     * @param user
     * @param password
     * @param host
     * @param queue
     */
    public Consumer(String user, String password, String host, String vhost, int port, boolean ssl, String queue) throws Exception
    {
        this.user = user;
        this.password = password;
        this.host = host;
        this.queue = queue;
        this.port = port;
        this.vhost = vhost;
        this.ssl = ssl;

        this.factory = new ConnectionFactory();
        this.factory.setHost(this.host);
        if (this.ssl) {
            this.factory.useSslProtocol();
        }
        this.createConsumer();
    }

    private void createConsumer() throws Exception {
        this.factory.setPassword(this.password);
        this.factory.setUsername(this.user);
        this.factory.setVirtualHost(this.vhost);
        this.factory.setPort(this.port);
        this.factory.setRequestedHeartbeat(0);
        this.factory.setConnectionTimeout(0);

        this.connection = this.factory.newConnection();
        this.channel = connection.createChannel();
        this.consumer = new QueueingConsumer(channel);
        this.channel.basicConsume(this.queue, true, this.consumer);
        System.out.println("AMQP "+this.host+" on SSL "+this.factory.isSSL()+" consumer is running on queue : " + this.queue);
    }

    /**
     * @param listener
     */
    public void addAMQPMessageListener(AMQPMessageListener listener) {
        amqpMessageListenerCollection.add(listener);
    }

    /**
     * @param listener
     */
    public void addAMQPErrorListener(AMQPConsumerErrorListener listener) {
        amqpConsumerErrorListener.add(listener);
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                QueueingConsumer.Delivery delivery = this.consumer.nextDelivery();
                String message = new String(delivery.getBody());
                AMQPMessage amqpMessage = new AMQPMessage(message);
                for (AMQPMessageListenerInterface listener : this.amqpMessageListenerCollection) {
                    listener.onMessageReceived(amqpMessage);
                }
            }
        } catch (Exception exception) {
            for (AMQPConsumerErrorListenerInterface listener : this.amqpConsumerErrorListener) {
                listener.onErrorReceved(exception);
            }
        }
    }

    public void cancel() {
        try {
            this.channel.close();
            this.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interrupt();
    }
}
