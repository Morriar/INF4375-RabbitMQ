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

import json.*;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Date;

public class Logger {

    private String loggerId;

    private String loggerExchange;

    private Channel loggerChannel;

    public Logger(String loggerId, String loggerExchange, Channel loggerChannel) {
        this.loggerId = loggerId;
        this.loggerExchange = loggerExchange;
        this.loggerChannel = loggerChannel;
    }

    public void log(String message) {
        LogMessage log = new LogMessage(new Date(), loggerId, message);
        System.out.println(log.toString());
        sendLog(log);
    }

    private void sendLog(LogMessage log) {
        try {
            loggerChannel.basicPublish(loggerExchange, "", null, log.toJson().toString().getBytes());
        } catch (IOException ex) {
            System.err.println("Unable to send log message " + log.toString());
            System.err.println(ex.getMessage());
        }
    }
}
