package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.MessageHandler;

/**
 * 错误消息，调度失败后执行，随后执行延迟消息{@link DelayMessage}
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class FailureMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 FailureMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_FAILURE;
        return message;
    }

    /**
     * 执行到错误消息后，发送延迟消息，等待指定事件后，再次尝试操作任务
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {

        if (callback == null) {
            return;
        }

        MessageHandler handler = callback.loadMessageHandler();
        handler.sendMessage(DefaultMessageGroup.TYPE_DELAY);
    }
}
