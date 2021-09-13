package org.sheedon.uploader.message;

import android.os.Message;

import org.sheedon.uploader.MessageHandler;

/**
 * 延迟消息，发生在执行失败后调用，采用DELAY_TIME的方式延迟处理
 * 失败时事件依次添加
 * 成功时依次减少
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 11:42 上午
 */
class DelayMessage implements MessageStrategy {

    // 延迟事件
    private static final long[] DELAY_TIME = new long[]{5 * 1000, 5 * 1000, 10 * 1000,
            15 * 1000, 25 * 1000, 40 * 1000,
            75 * 1000, 105 * 1000, 170 * 1000};

    // 超时时间
    public static final int TIMEOUT = 30000;

    // 下标
    private static int index;

    /**
     * 从全局池中返回一个新的 DelayMessage类型 的消息实例。 允许我们在许多情况下避免分配新对象
     *
     * @return Message 消息
     */
    @Override
    public Message obtainMessage() {
        Message message = Message.obtain();
        message.what = DefaultMessageGroup.TYPE_DELAY;
        return message;
    }

    /**
     * 错误消息执行到达此处，按照「斐波那列基数」做延迟操作基准延长或缩短
     *
     * @param callback 给消息处理需要提供的行为
     */
    @Override
    public void handleEvent(OnMessageBehavior callback) {
        if (callback == null) {
            return;
        }

        MessageHandler handler = callback.loadMessageHandler();
        handler.sendDelayMessage(DefaultMessageGroup.TYPE_CHECK, getDelayTime());
    }

    /**
     * 获取延迟时间
     */
    public static long getDelayTime() {
        if (index >= DELAY_TIME.length) {
            index = DELAY_TIME.length - 1;
        }
        return DELAY_TIME[index];
    }

    /**
     * 重置延迟时间
     */
    public static void resetDelayTime() {
        if (index > 0) {
            index--;
        }
    }
}
