package org.sheedon.uploader;

/**
 * 事件监听器，由外部判定是否由资源和调度事件
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:11 下午
 */
public interface EventListener {

    /**
     * 是否有资源
     */
    boolean hasSource();


    /**
     * 处理事件
     */
    void handleEvent(MessageHandleCenter center);
}
