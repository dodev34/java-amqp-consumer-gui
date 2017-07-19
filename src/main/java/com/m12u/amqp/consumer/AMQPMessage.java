package com.m12u.amqp.consumer;

/**
 * Created by m12u on 18/07/2017.
 */
public class AMQPMessage {

    private String message;

    public AMQPMessage(String message) {
        this.message = message;
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }
}
