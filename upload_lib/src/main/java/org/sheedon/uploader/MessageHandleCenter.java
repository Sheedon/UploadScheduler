package org.sheedon.uploader;

/**
 * 消息处理器对外提供的接口
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 2:49 下午
 */
public interface MessageHandleCenter {

    /**
     * 发送处理成功
     */
    void sendSuccessMessage();

    /**
     * 发送处理失败
     */
    void sendFailureMessage();
}
