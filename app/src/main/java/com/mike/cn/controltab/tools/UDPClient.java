package com.mike.cn.controltab.tools;

import static com.mike.cn.controltab.app.ConnectConfig.IP_ADDS;
import static com.mike.cn.controltab.app.ConnectConfig.PORT_NUM;

import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.mike.cn.controltab.app.ConnectConfig;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Created by lenovo on 2016/2/23.
 */
public class UDPClient implements Runnable {
    int UDP_PORT = 9999;
    String HOST_IP = "192.168.22.120";
    public final static String ERROR_TAG = "ERROR";
    public final static String MES_TAG = "MES";
    private static DatagramSocket socket = null;
    DatagramPacket packetRcv = null;
    InetAddress hostAddress = null;
    /**
     * udp生命线程
     */
    private boolean udpLife = true;
    /**
     * 接收消息
     */
    private final byte[] msgRcv = new byte[1024];

    public UDPClient() {
        super();
        UDP_PORT = MMKV.defaultMMKV().getInt(PORT_NUM, 9999);
        HOST_IP = MMKV.defaultMMKV().getString(IP_ADDS, "");
    }

    /**
     * 返回udp生命线程因子是否存活
     */
    public boolean isUdpLife() {
        return udpLife;
    }


    /**
     * 设置连接
     */
    public void connect() {
        try {
            hostAddress = InetAddress.getByName(HOST_IP);
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
            packetRcv = new DatagramPacket(msgRcv, msgRcv.length);
            packetRcv.setAddress(hostAddress);
        } catch (UnknownHostException e) {
            Log.i("udpClient", "未找到服务器");
            LiveEventBus.get(ERROR_TAG).post("未找到服务器");
            e.printStackTrace();
        } catch (SocketException e) {
            Log.i("udpClient", "建立接收数据报失败");
            LiveEventBus.get(ERROR_TAG).post("建立接收数据报失败");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("udpClient", "建立连接错误");
            LiveEventBus.get(ERROR_TAG).post("建立连接错误");
        }
    }

    /**
     * 更改UDP生命线程因子
     */
    public void setUdpLife(boolean b) {
        udpLife = b;
    }

    /**
     * 发送消息
     */
    public String send(String msgSend) {
        DatagramPacket packetSend = new DatagramPacket(msgSend.getBytes(), msgSend.getBytes().length, hostAddress, UDP_PORT);
        try {
            socket.send(packetSend);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("udpClient", "发送失败");
            LiveEventBus.get(ERROR_TAG).post("发送失败");
        }
        return msgSend;
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        Log.i("udpClient", "UDP监听关闭");
        socket.close();
    }

    @Override
    public void run() {
        try {
            if (socket == null) {
                socket = new DatagramSocket();
            }
        } catch (SocketException e) {
            Log.i("udpClient", "建立接收数据报失败");
            e.printStackTrace();
        }
        while (udpLife) {
            try {
                Log.i("udpClient", "UDP监听");
                socket.receive(packetRcv);
                String rcvMsg = new String(packetRcv.getData(), packetRcv.getOffset(), packetRcv.getLength(), StandardCharsets.UTF_8);
                LiveEventBus.get(MES_TAG).post(rcvMsg);
                Log.i("Rcv", rcvMsg);
            } catch (IOException e) {
//                e.printStackTrace();
            } catch (Exception e) {
                udpLife = false;
                e.printStackTrace();
            }
        }


    }
}
