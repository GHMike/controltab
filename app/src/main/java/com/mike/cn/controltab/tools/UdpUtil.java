package com.mike.cn.controltab.tools;

import static com.mike.cn.controltab.app.ConnectConfig.IP_ADDS;
import static com.mike.cn.controltab.app.ConnectConfig.PORT_NUM;

import android.util.Log;

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
    // UDP端口
    private static int UDP_PORT = 12345;
    // IP 地址
    private static String HOST_IP = "";
    // 最大数据包长度
    private static final int MAX_UDP_DATAGRAM_LEN = 1024;

    private DatagramSocket udpSocket;
    //发送线程池
    private ExecutorService executorService;
    //接收线程池
    private ExecutorService executorService2;
    private UdpReceiveListener thisUdpReceiveListener;

    private UdpUtil() {
        try {
            udpSocket = new DatagramSocket();
            executorService = Executors.newSingleThreadExecutor();
            executorService2 = Executors.newSingleThreadExecutor();
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
        thisUdpReceiveListener = listener;
        ReceiveUdpTask receiveUdpTask = new ReceiveUdpTask();
        executorService2.submit(receiveUdpTask);
    }

    public UdpReceiveListener getThisUdpReceiveListener() {
        return thisUdpReceiveListener;
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
        if (executorService2 != null) {
            executorService2.shutdownNow();
            executorService2 = null;
        }
    }

    private class SendUdpTask implements Runnable {
        private String command;

        SendUdpTask(String command) {
            this.command = command;
        }

        @Override
        public void run() {


            byte[] sendData = command.getBytes();
            InetAddress destinationAddress;
            try {
                if (udpSocket == null) {
                    udpSocket = new DatagramSocket();
                    return;
                }
                UDP_PORT = MMKV.defaultMMKV().getInt(PORT_NUM, 9999);
                HOST_IP = MMKV.defaultMMKV().getString(IP_ADDS, "192.168.0.1");
                destinationAddress = InetAddress.getByName(HOST_IP); // 指定接收端IP地址
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destinationAddress, UDP_PORT);
                udpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveUdpTask implements Runnable {
        @Override
        public void run() {
            try {
                if (udpSocket == null) {
                    udpSocket = new DatagramSocket();
                }
                byte[] buffer = new byte[MAX_UDP_DATAGRAM_LEN];
                DatagramPacket packetRcv = new DatagramPacket(buffer, buffer.length);
                InetAddress hostAddress = InetAddress.getByName(HOST_IP);
                packetRcv.setAddress(hostAddress);

                while (!Thread.currentThread().isInterrupted()) {
                    udpSocket.receive(packetRcv);
                    String receivedData = new String(packetRcv.getData(), 0, packetRcv.getLength());
                    if (thisUdpReceiveListener != null) {
                        thisUdpReceiveListener.onUdpReceived(receivedData);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
