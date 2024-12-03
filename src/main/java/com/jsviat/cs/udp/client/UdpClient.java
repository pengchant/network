package com.jsviat.cs.udp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int uid;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new UdpClient().createAndShowGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public UdpClient() throws IOException {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(SERVER_ADDRESS);
        uid = socket.getLocalPort();
    }

    private void createAndShowGUI() {
        frame = new JFrame("UDP 聊天室, 当前用户 (" + this.uid + ")");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField = new JTextField();
        textField.addActionListener(this::sendMessage);
        frame.add(textField, BorderLayout.SOUTH);

        frame.setVisible(true);

        new Thread(this::receiveMessages).start();
    }

    private void sendMessage(ActionEvent e) {
        String message = textField.getText();
        try {
            if (message.trim().isEmpty()) {
                return;
            }
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length,
                    serverAddress, SERVER_PORT);
            socket.send(packet);
            textField.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0,
                        receivePacket.getLength(), StandardCharsets.UTF_8);
                System.out.println("message: " + message);
                textArea.append(message + "\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
