package org.sheedon.uploader;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sheedon.uploader.message.DefaultMessageGroup;
import org.sheedon.uploader.message.MessageStrategy;


/**
 * 消息执行者，借助{@link DefaultMessageGroup} 构建消息，并执行发送实现{@link MessageStrategy}的对象，
 * 该MessageHandler与线程AsyncTrigger的HandlerThread所在线程的消息队列相关联，并在它们从消息中出来时执行它们队列。
 * 主要将要在线程不同的线程上执行的操作排入队列。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 9:57 上午
 */
public class MessageHandler implements MessageHandleCenter {

    // 绑定到 AsyncTrigger 中进行消息执行
    private final Handler handler;

    // 当前状态，控制业务客户端，防止多次发送任务添加，导致消息处理执行出错
    @DefaultMessageGroup.StatusType
    private int status = DefaultMessageGroup.TYPE_NORMAL;

    /**
     * 消息执行器
     *
     * @param looper   异步线程Looper
     * @param callback Handler回调
     */
    MessageHandler(@NonNull Looper looper, @Nullable Handler.Callback callback) {
        handler = new Handler(looper, callback);
    }

    /**
     * 从子调度器中传来数据，若当前状态为 DefaultMessageGroup.DEFAULT 则可执行，否则等待
     * 防止重复发送消息，导致消息发送出现意料之外的问题
     */
    void sendDelayMessage() {
        if (status != DefaultMessageGroup.TYPE_NORMAL) {
            return;
        }

        // 发送核实消息
        status = DefaultMessageGroup.TYPE_CHECK;
        MessageStrategy strategy = DefaultMessageGroup.obtainByStatus(DefaultMessageGroup.TYPE_CHECK);
        handler.sendMessage(strategy.obtainMessage());
    }

    /**
     * 子调度器发送处理成功，从而发送「完成消息」
     */
    @Override
    public void sendSuccessMessage() {
        if (status != DefaultMessageGroup.TYPE_EXECUTE
                && status != DefaultMessageGroup.TYPE_TIMEOUT) {
            return;
        }
        status = DefaultMessageGroup.TYPE_COMPLETE;
        MessageStrategy strategy = DefaultMessageGroup.obtainByStatus(DefaultMessageGroup.TYPE_COMPLETE);
        handler.sendMessage(strategy.obtainMessage());

    }

    /**
     * 子调度器发送处理失败，从而发送「失败消息」
     */
    @Override
    public void sendFailureMessage() {
        if (status != DefaultMessageGroup.TYPE_EXECUTE) {
            return;
        }
        status = DefaultMessageGroup.TYPE_FAILURE;
        MessageStrategy strategy = DefaultMessageGroup.obtainByStatus(DefaultMessageGroup.TYPE_FAILURE);
        handler.sendMessage(strategy.obtainMessage());
    }

    /**
     * 移除消息，存在则移除
     * 主要为了移除超时消息
     *
     * @param status 消息状态
     */
    public void removeMessage(int status) {
        if (handler.hasMessages(status)) {
            handler.removeMessages(status);
        }
    }

    /**
     * 附加当前状态，更新
     *
     * @param status 状态
     */
    void attachStatus(int status) {
        this.status = status;
    }

    /**
     * 通过状态类型，发送对应消息
     *
     * @param status 状态
     */
    public void sendMessage(int status) {
        attachStatus(status);
        MessageStrategy strategy = DefaultMessageGroup.obtainByStatus(status);
        handler.sendMessage(strategy.obtainMessage());
    }

    /**
     * 发送延迟消息,指定的状态按 延迟 delayMillis 发送
     *
     * @param status 状态
     */
    public void sendDelayMessage(int status, long delayMillis) {
        attachStatus(status);
        handler.sendEmptyMessageDelayed(status, delayMillis);
    }

    /**
     * 执行错误处理，一般不会执行到这里，执行到，说明也差不多是出问题了
     */
    void handleError() {
        MessageStrategy strategy = DefaultMessageGroup.obtainByStatus(DefaultMessageGroup.TYPE_COMPLETE);
        handler.sendMessage(strategy.obtainMessage());
    }
}
