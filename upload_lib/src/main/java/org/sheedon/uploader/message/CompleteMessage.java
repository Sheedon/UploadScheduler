package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.MessageHandler;

/**
 * 执行完成消息，移除超时{@link TimeOutMessage}
 * 并且执行核实操作{@link CheckMessage}
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class CompleteMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 CompleteMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_COMPLETE;
        return message;
    }

    /**
     * 执行完成消息，移除超时消息，并且将流程过渡到 核实消息上
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {
        if (callback == null) {
            return;
        }

        // 移除超时消息
        MessageHandler handler = callback.loadMessageHandler();
        handler.removeMessage(DefaultMessageGroup.TYPE_TIMEOUT);
        // 重置延迟指标
        DelayMessage.resetDelayTime();

        // 发送核实消息
        handler.sendMessage(DefaultMessageGroup.TYPE_CHECK);
    }
}
