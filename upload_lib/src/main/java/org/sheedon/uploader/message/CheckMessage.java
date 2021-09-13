package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.EventQueue;
import org.sheedon.uploader.MessageHandler;

/**
 * 核实消息，判断是否有存在调度事件，
 * 若存在，则执行{@link ExecuteMessage}消息，执行事件
 * 否则，执行回到{@link NormalMessage} 初始状态
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class CheckMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 CheckMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_CHECK;
        return message;
    }

    /**
     * 核实是否存在事件，存在则执行，不存在，回到初始状态
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {
        if (callback == null)
            return;

        EventQueue queue = callback.loadEventQueue();

        // 核实是否存在事件
        // 不存在，回到初始状态
        boolean hasEvent = queue.hasEvent();
        if (!hasEvent) {
            MessageHandler handler = callback.loadMessageHandler();
            handler.sendMessage(DefaultMessageGroup.TYPE_NORMAL);
            return;
        }

        // 存在则执行
        MessageHandler handler = callback.loadMessageHandler();
        handler.sendMessage(DefaultMessageGroup.TYPE_EXECUTE);
    }
}
