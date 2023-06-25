package org.sheedon.uploader;

/**
 * 网络是否连上
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2023/6/25 14:56
 */
public interface INetConnected {

    /**
     * 网络是否连上
     *
     * @return true 连上，false 未连上
     */
    boolean isConnected();
}
