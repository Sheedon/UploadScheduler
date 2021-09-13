package org.sheedon.uploader.message;

import android.os.Message;

/**
 * 销毁消息，暂时不执行特殊操作，代表当前状态也是结束
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class DestroyMessage implements MessageStrategy {

    /**
     * 从全局池中返回一个新的 DestroyMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_DESTROY;
        return message;
    }

    /**
     * 销毁消息，不执行特殊操作，就是代表当前状态也是结束
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {

    }
}
