package com.m12u.amqp.listener;

import java.util.EventListener;

/**
 * Created by m12u on 19/07/2017.
 */
public interface AMQPConsumerErrorListenerInterface extends EventListener {

    public void onErrorReceved(Exception exception);
}
