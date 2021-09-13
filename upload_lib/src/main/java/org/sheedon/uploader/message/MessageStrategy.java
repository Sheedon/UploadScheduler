package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.EventQueue;
import org.sheedon.uploader.MessageHandler;
import org.sheedon.uploader.process.AbstractProcess;

/**
 * 消息策略,两个职责，
 * 1. 创建消息
 * 2. 执行事件
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/11 11:58 下午
 */
public interface MessageStrategy {


    /**
     * @return 获取一个新消息
     */
    Message obtainMessage();

    /**
     * 执行一个事件
     */
    void handleEvent(OnMessageBehavior callback);

    /**
     * 给消息处理需要提供的行为
     * 消息处理器，事件队列，流程处理器
     */
    interface OnMessageBehavior {

        /**
         * 获取消息执行器，用于执行下一个任务
         */
        MessageHandler loadMessageHandler();

        /**
         * 加载事件队列
         */
        EventQueue loadEventQueue();

        /**
         * 加载流程处理
         */
        AbstractProcess loadAbstractProcess();
    }
}
