package com.m12u.amqp;

import javax.swing.*;

import com.m12u.amqp.consumer.AMQPMessage;
import com.m12u.amqp.consumer.Consumer;
import com.m12u.amqp.listener.AMQPConsumerErrorListener;
import com.m12u.amqp.listener.AMQPMessageListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AMQP extends JDialog {
    private JPanel contentPane;
    private JTabbedPane tabConcumer;
    private JPanel tabConnection;
    private JPanel tabMessage;
    private JLabel label_host;
    private JTextField text_host;
    private JLabel label_port;
    private JTextField text_port;
    private JLabel label_user;
    private JTextField text_user;
    private JLabel label_password;
    private JPasswordField text_password;
    private JButton connexionButton;
    private JTextArea textArea;
    private JLabel label_queue;
    private JTextField text_queue;
    private JLabel labelQueueName;
    private JLabel labelIsConnected;
    private JCheckBox checkboxSSL;
    private JTextField textVHost;
    private JLabel labelCurentHost;
    private JButton buttonDeconnection;

    // listener
    private AMQPMessageListener amqpMessageListener;
    private AMQPConsumerErrorListener amqpConsumerError;

    // consumer
    private Consumer consumer;
    private static final String ON_LINE = "You are connected to the server";
    private static final String OFF_LINE = "You are not connected";

    public AMQP() {
        setContentPane(contentPane);
        setModal(true);

        amqpMessageListener = new AMQPMessageListener() {
            @Override
            public void onMessageReceived(AMQPMessage message) {
                textArea.append(message.getMessage()+"\n");
            }
        };

        amqpConsumerError = new AMQPConsumerErrorListener() {
            @Override
            public void onErrorReceved(Exception exception) {
                clearConsumer();
                labelIsConnected.setForeground(Color.RED);
                labelIsConnected.setText(exception.getMessage());
            }
        };

        connexionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearConsumer();
                createConsumer();
            }
        });
        buttonDeconnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearConsumer();
            }
        });
    }

    /**
     *
     */
    protected void createConsumer() {
        boolean ssl = checkboxSSL.isSelected();
        String host = text_host.getText();
        String user = text_user.getText();
        String password = text_password.getText();
        String queue = text_queue.getText();
        String vhost = textVHost.getText();
        int port = Integer.parseInt(text_port.getText());
        try {
            consumer = new Consumer(user, password, host, vhost, port, ssl, queue);
            consumer.addAMQPMessageListener(amqpMessageListener);
            consumer.addAMQPErrorListener(amqpConsumerError);
            consumer.start();
            labelIsConnected.setForeground(Color.GREEN);
            labelIsConnected.setText(ON_LINE+" " + text_host.getText());
            labelQueueName.setText(queue);
            labelCurentHost.setText(host);
        } catch (Exception e1) {
            amqpConsumerError.onErrorReceved(e1);
        }
    }

    /**
     *
     */
    protected void clearConsumer() {
        if (consumer instanceof Consumer) {
            this.consumer.cancel();
        }
        this.consumer = null;
        this.labelQueueName.setText("");
        this.labelCurentHost.setText("");
        this.textArea.setText("");
        this.labelIsConnected.setForeground(Color.BLACK);
        this.labelIsConnected.setText(OFF_LINE);
    }
}
