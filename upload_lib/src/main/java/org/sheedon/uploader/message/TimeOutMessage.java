package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.MessageHandler;


/**
 * 超时消息，到达这里，则直接反馈到失败
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class TimeOutMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 TimeOutMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_TIMEOUT;
        return message;
    }

    /**
     * 发送 「错误消息」
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {
        if (callback == null)
            return;

        MessageHandler handler = callback.loadMessageHandler();
        handler.sendMessage(DefaultMessageGroup.TYPE_FAILURE);
    }
}
