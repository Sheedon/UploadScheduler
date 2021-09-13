package org.sheedon.uploader.message;

import android.os.Message;

/**
 * 默认消息，当前不执行额外操作
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class NormalMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 NormalMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_NORMAL;
        return message;
    }

    /**
     * 当前无需操作
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {

    }
}
