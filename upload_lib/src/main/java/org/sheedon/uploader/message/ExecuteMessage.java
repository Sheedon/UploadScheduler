package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.EventQueue;
import org.sheedon.uploader.MessageHandler;
import org.sheedon.uploader.process.AbstractProcess;

/**
 * 执行消息，调度AbstractProcess来执行操作
 * 若调度失败，则返回{@link FailureMessage}
 * 否则，启动超时消息{@link TimeOutMessage}防止处理没有反馈
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class ExecuteMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 ExecuteMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_EXECUTE;
        return message;
    }

    /**
     * 执行到「执行消息」后，拿到事件key，拿不到则执行错误，发送错误消息
     * 执行任务失败则发送错误消息，否则发送超时消息，防止操作不反馈
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {
        if (callback == null) {
            return;
        }

        // 拿到事件key，拿不到则执行错误，发送错误消息
        EventQueue queue = callback.loadEventQueue();
        String eventName = queue.pollEvent();
        MessageHandler handler = callback.loadMessageHandler();
        if (eventName == null || eventName.trim().isEmpty()) {
            handler.sendMessage(DefaultMessageGroup.TYPE_FAILURE);
            return;
        }

        // 执行任务，执行失败则发送错误消息
        AbstractProcess process = callback.loadAbstractProcess();
        boolean execute = process.execute();
        if (!execute) {
            handler.sendMessage(DefaultMessageGroup.TYPE_FAILURE);
            return;
        }

        // 发送超时消息，防止操作不反馈
        handler.sendDelayMessage(DefaultMessageGroup.TYPE_TIMEOUT, DefaultMessageGroup.TIMEOUT);
    }
}
