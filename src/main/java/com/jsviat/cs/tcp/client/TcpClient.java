package com.jsviat.cs.tcp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 打包成jar包后: java "-Dfile.encoding=UTF-8" -jar .\tcp-client.jar --port 12345 --host localhost
 */
public class TcpClient {
    private String SERVER_ADDRESS = "localhost";
    private int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int uid;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        // 手动解析命令行参数
        for (int i = 0; i < args.length; i++) {
            if ("--host".equals(args[i]) && i + 1 < args.length) {
                host = args[i + 1];
                i++; // 跳过下一个值，因为已经处理过
            } else if ("--port".equals(args[i]) && i + 1 < args.length) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("端口号必须是有效的整数");
                    return;
                }
                i++; // 跳过下一个值，因为已经处理过
            }
        }

        // 输出解析的结果
        System.out.println("参数：Host: " + host);
        System.out.println("参数：Port: " + port);


        // 启动客户端服务
        String finalHost = host;
        int finalPort = port;
        SwingUtilities.invokeLater(() -> {
            try {
                new TcpClient().createAndShowGUI(finalHost, finalPort);
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

    private void createAndShowGUI(String host, int port) {
        if (host != null && !host.isEmpty()) {
            SERVER_ADDRESS = host;
        }
        if (port > 0) {
            SERVER_PORT = port;
        }

        System.out.println("===>当前host:" + SERVER_ADDRESS + "，当前端口：" + SERVER_PORT);

        frame = new JFrame("TCP 聊天室,当前用户(" + this.uid + ")");
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
