package com.hxht;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Server extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JButton button;

	private MqttClient client;
	private String host = "tcp://192.168.14.206:1883";
	private String userName = "admin";
	private String passWord = "password";
	private MqttTopic topic;
	private MqttMessage message;

	private String myTopic = "test/topic";

	public Server() {

		try {
			client = new MqttClient(host, "Server", new MemoryPersistence());
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Container container = this.getContentPane();
		panel = new JPanel();
		button = new JButton("发布话题");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					MqttDeliveryToken token = topic.publish(message);
					token.waitForCompletion();
					System.out.println(token.isComplete() + "========");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel.add(button);
		container.add(panel, "North");

	}

	private void connect() {

		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(passWord.toCharArray());
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(20);
		try {
			client.setCallback(new MqttCallback() {

				@Override
				public void connectionLost(Throwable cause) {
					System.out.println("connectionLost-----------");
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					System.out.println("deliveryComplete---------"
							+ token.isComplete());
				}

				@Override
				public void messageArrived(String topic, MqttMessage arg1)
						throws Exception {
					System.out.println("messageArrived----------");

				}
			});

			topic = client.getTopic(myTopic);

			message = new MqttMessage();
			message.setQos(1);
			message.setRetained(true);
			System.out.println(message.isRetained() + "------ratained状态");
			message.setPayload("FlyingSnow2211----Forever".getBytes());

			client.connect(options);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Server s = new Server();
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.setSize(600, 370);
		s.setLocationRelativeTo(null);
		s.setVisible(true);
	}
}
