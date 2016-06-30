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
package log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class RandomLogProducer {

    private final static String URI = "amqp://vmlqfqbm:7quut0vtSXD_Hz9lIgnn2g3Zs4feQnvX@jellyfish.rmq.cloudamqp.com/vmlqfqbm";

    private final static String EXCHANGE_NAME = "log";

    public static void main(String[] args) throws InterruptedException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {

        if (args.length != 1) {
            System.out.println("Expected exactly one arg: the logger id");
            System.exit(1);
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        Logger logger = new Logger(args[0], EXCHANGE_NAME, channel);
        Random generator = new Random();

        System.out.println("[*] Sending random numbers...");
        while (true) {
            int rand = generator.nextInt(100);
            if (rand > 60) {
                logger.log("Rand: " + rand);
            }
            Thread.sleep(500);
        }
    }
}
