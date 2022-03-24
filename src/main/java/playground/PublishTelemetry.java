package playground;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PublishTelemetry {
    public static void main(String[] args) {
        String access_token = "cOU35qr3PApqkYxFUQF7";

        // Topic we want to publish to
        String topic = "v1/devices/me/telemetry";
        // Set dummy contents
        // String content = "{\"Memory\" : 4.6, \"Cpu\" : 56, \"Temp\" : 45}";
        String content= "{\"Temp\":33.5,\"Humi\":60.7}";
        int qos = 0;
        // Where thingsboard listen data
        String broker = "tcp://demo.thingsboard.io:1883";
        String clientId = "TDC1";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setKeepAliveInterval(60);
            connectOptions.setUserName(access_token);
            System.out.println("Connecting to broker: " + broker);
            client.connect(connectOptions);
            System.out.println("Connected to thingsboard broker");
            System.out.println("Publishing message:" + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            client.publish(topic, message);
            System.out.println("Message published ");
            System.out.println("Please check data in telemetry of your device on thingsboard");
        } catch (MqttException e) {
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
    }
}
