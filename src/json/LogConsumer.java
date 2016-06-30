/*
 * Copyright 2016 Alexandre Terrasa <alexandre@moz-code.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package json;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.concurrent.TimeoutException;

public class LogConsumer {

    private final static String URI = "";

    private final static String QUEUE_NAME = "log";

    public static void main(String[] argv)
            throws java.io.IOException,
            java.lang.InterruptedException,
            TimeoutException,
            URISyntaxException,
            NoSuchAlgorithmException,
            KeyManagementException {

        // Prepare connection to CloudAMQP
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare queue
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Receive messages
        System.out.println(" [*] Waiting for messages...");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                    String message = new String(body, "UTF-8");
                    LogMessage log = new LogMessage(message);
                    System.out.println(log.toString());
                } catch (ParseException ex) {
                    System.err.println("Unable to parse log message");
                    System.err.println(ex.getMessage());
                }
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
