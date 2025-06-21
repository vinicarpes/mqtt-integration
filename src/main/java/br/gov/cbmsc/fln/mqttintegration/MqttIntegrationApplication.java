package br.gov.cbmsc.fln.mqttintegration;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqttIntegrationApplication {

    public static void main(String[] args) {
        String broker = "tcp://broker.emqx.io:1883";
        String clientId = "demo_client";
        String topic = "topic/test";
        String topic2 = "powercats/channels/id/sensor/id";
        int subQos = 1;
        int pubQos = 1;
        String msg = "Ola MQTT";
        String msg2 = "458.7";

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            client.connect(options);

            if (client.isConnected()) {
                client.setCallback(new MqttCallback() {
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("topic: " + topic);
                        System.out.println("qos: " + message.getQos());
                        System.out.println("message content: " + new String(message.getPayload()));
                    }

                    public void connectionLost(Throwable cause) {
                        System.out.println("connectionLost: " + cause.getMessage());
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("deliveryComplete: " + token.isComplete());
                    }
                });

                client.subscribe(topic, subQos);
                client.subscribe(topic2, pubQos);

                MqttMessage message = new MqttMessage(msg.getBytes());
                MqttMessage message2 = new MqttMessage(msg2.getBytes());
                message2.setQos(pubQos);
                message.setQos(pubQos);
                client.publish(topic, message);
                client.publish(topic2, message2);

            }

            client.disconnect();
            client.close();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}