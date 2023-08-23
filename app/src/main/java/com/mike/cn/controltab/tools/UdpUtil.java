package com.mike.cn.controltab.tools;

import static com.mike.cn.controltab.app.ConnectConfig.IP_ADDS;
import static com.mike.cn.controltab.app.ConnectConfig.PORT_NUM;

import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UdpUtil {

    private static UdpUtil instance;

    private static int UDP_PORT = 12345; // UDP端口
    private static String HOST_IP = ""; // IP 地址
    private static int MAX_UDP_DATAGRAM_LEN = 1024; // 最大数据包长度

    private DatagramSocket udpSocket;
    private ExecutorService executorService;

    private UdpUtil() {
        try {
            udpSocket = new DatagramSocket();
            executorService = Executors.newSingleThreadExecutor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized UdpUtil getInstance() {
        if (instance == null) {
            instance = new UdpUtil();
        }
        return instance;
    }

    public void sendUdpCommand(String command) {
        SendUdpTask sendUdpTask = new SendUdpTask(command);
        executorService.submit(sendUdpTask);
    }

    public interface UdpReceiveListener {
        void onUdpReceived(String data);
    }

    public void startListening(UdpReceiveListener listener) {
        ReceiveUdpTask receiveUdpTask = new ReceiveUdpTask(listener);
        Future<?> future = executorService.submit(receiveUdpTask);
    }

    public void stopListening() {
        if (udpSocket != null) {
            udpSocket.close();
            udpSocket = null;
        }
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    private class SendUdpTask implements Runnable {
        private String command;

        SendUdpTask(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            if (udpSocket == null) {
                return;
            }

            byte[] sendData = command.getBytes();
            InetAddress destinationAddress;
            try {
                UDP_PORT = MMKV.defaultMMKV().getInt(PORT_NUM, 9999);
                HOST_IP = MMKV.defaultMMKV().getString(IP_ADDS, "");
                destinationAddress = InetAddress.getByName(HOST_IP); // 指定接收端IP地址
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destinationAddress, UDP_PORT);
                udpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveUdpTask implements Runnable {
        private UdpReceiveListener listener;

        ReceiveUdpTask(UdpReceiveListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            if (udpSocket == null) {
                return;
            }

            byte[] buffer = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    udpSocket.receive(packet);
                    String receivedData = new String(packet.getData(), 0, packet.getLength());
                    if (listener != null) {
                        listener.onUdpReceived(receivedData);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
