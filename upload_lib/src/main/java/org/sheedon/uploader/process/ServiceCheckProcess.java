package org.sheedon.uploader.process;

import android.os.SystemClock;

import org.sheedon.uploader.INetConnected;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * 服务器连接检测流程，通过发起一个连接服务器请求，通过code == 200 来判断网络是否连接成功
 * 为执行真实调度的上一步
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:47 下午
 */
public class ServiceCheckProcess extends AbstractProcess {

    // 服务器地址
    private final String baseUrl;
    // 上一次操作时间
    private long lastHandleTime;
    // 间隔时间
    private static final int INTERVAL = 10000;
    // 上一次网络情况
    private boolean isConnect;

    private final INetConnected netConnected;

    ServiceCheckProcess(String baseUrl, INetConnected netConnected) {
        if (netConnected != null) {
            this.netConnected = netConnected;
            this.baseUrl = null;
        } else if (baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl;
            this.netConnected = null;
        } else {
            this.baseUrl = baseUrl + "/";
            this.netConnected = null;
        }

    }

    /**
     * 执行任务，核实是否可以连接上服务器
     *
     * @return 是否执行成功
     */
    @Override
    public boolean execute() {
        if (!checkServiceConnection()) {
            return false;
        }

        if (nextProcess != null) {
            return nextProcess.execute();
        }
        return false;
    }

    /**
     * 核实网络连接情况
     * 连接成功的清空下，10秒内只检测一次
     */
    private boolean checkServiceConnection() {
        long nowTime = SystemClock.uptimeMillis();
        if (nowTime - lastHandleTime <= INTERVAL && isConnect) {
            return true;
        }

        while (!(isConnect = checkConnected())) {
            try {
                TimeUnit.MILLISECONDS.sleep(3 * INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastHandleTime = nowTime;

        return true;
    }

    private boolean checkConnected() {
        if (netConnected == null) {
            return connByNetService(baseUrl);
        }
        return netConnected.isConnected();
    }

    /**
     * 判断是否连接到服务器，通过指定url的超时3秒连接，code为200表示可以连上服务器
     *
     * @return boolean 是否可以连上服务器
     */
    private static boolean connByNetService(String urlString) {
        URL url;
        HttpURLConnection conn = null;
        boolean isConnect = false;
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 3);
            conn.setReadTimeout(1000 * 10);
            conn.setRequestMethod("GET");
            int s = conn.getResponseCode();
            isConnect = s < 500;
        } catch (IOException ignored) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return isConnect;
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        if (nextProcess != null) {
            nextProcess.onDestroy();
        }
    }
}
