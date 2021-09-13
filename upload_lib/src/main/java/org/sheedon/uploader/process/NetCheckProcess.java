package org.sheedon.uploader.process;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

/**
 * 网络检测流程，主要核实当前是否网络是否连接成功，
 * 为验证是否连接上服务的上一步
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:45 下午
 */
final class NetCheckProcess extends AbstractProcess {

    // 网络更改反馈的通知
    private final ConnectivityManager.NetworkCallback networkCallback;
    // 回答有关网络连接状态的查询的类。
    private final ConnectivityManager connectivityManager;
    // 是否可用
    private boolean available;

    NetCheckProcess(Context context) {
        this.networkCallback = new NetworkCallbackImpl();
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);

    }

    /**
     * 检测网络是否可用
     */
    @Override
    public boolean execute() {
        if (!available) {
            return false;
        }
        if (nextProcess != null) {
            return nextProcess.execute();
        }
        return false;
    }

    /**
     * 消息反馈处理
     */
    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        /**
         * 网络可用
         */
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            available = true;
        }

        /**
         * 当框架出现网络硬丢失或正常故障结束时调用。
         * 不可用
         */
        @Override
        public void onLost(Network network) {
            super.onLost(network);
            available = false;
        }

        /**
         * 不可用
         */
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            available = false;
        }
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback);
        if (nextProcess != null) {
            nextProcess.onDestroy();
        }
    }
}
