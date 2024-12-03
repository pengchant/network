package com.jsviat.cs.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UdpServer {
    private static final int PORT = 12345;
    private static List<InetSocketAddress> clientAddresses = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("UDP Server started...");
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] receiveData = new byte[1024];

            while (true) {
                // 接收数据包
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                // 获取客户端的地址和消息
                InetSocketAddress clientAddress = new InetSocketAddress(receivePacket.getAddress(), receivePacket.getPort());
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Received from " + clientAddress + ": " + message);

                // 处理新客户端加入
                synchronized (clientAddresses) {
                    if (!clientAddresses.contains(clientAddress)) {
                        clientAddresses.add(clientAddress);
                    }
                }

                // 构造发送消息
                String sendMessage = String.format("%s 说: %s", clientAddress, message, "UTF-8");

                // 广播给所有已连接的客户端
                synchronized (clientAddresses) {
                    byte[] sendMessageBytes = sendMessage.getBytes(StandardCharsets.UTF_8); // 使用 UTF-8 编码
                    for (InetSocketAddress address : clientAddresses) {
                        DatagramPacket sendPacket = new DatagramPacket(
                                sendMessageBytes, sendMessageBytes.length,
                                address.getAddress(), address.getPort());
                        socket.send(sendPacket);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
