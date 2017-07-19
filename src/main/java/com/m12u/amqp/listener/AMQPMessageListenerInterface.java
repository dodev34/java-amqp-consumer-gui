package com.m12u.amqp.listener;

import com.m12u.amqp.consumer.AMQPMessage;

import java.util.EventListener;

/**
 *
 */
public interface AMQPMessageListenerInterface extends EventListener {

    public void onMessageReceived(AMQPMessage message);
}
