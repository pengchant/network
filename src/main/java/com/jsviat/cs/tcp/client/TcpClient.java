package com.jsviat.cs.tcp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int uid;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TcpClient().createAndShowGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public TcpClient() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        uid = socket.getLocalPort();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void createAndShowGUI() {
        frame = new JFrame("TCP 聊天室,当前用户("+this.uid+")");
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
        out.println(message);
        textField.setText("");
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                textArea.append(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
