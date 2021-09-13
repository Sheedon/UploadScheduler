package org.sheedon.uploader;

import android.os.HandlerThread;

import org.sheedon.uploader.message.DefaultMessageGroup;
import org.sheedon.uploader.message.MessageStrategy;
import org.sheedon.uploader.process.AbstractProcess;

/**
 * 异步触发器
 * 借助 {@link HandlerThread} 维持一个异步队列
 * 按顺序依次执行事件
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/11 11:36 下午
 */
public class AsyncTrigger {

    // 处理线程
    private final HandlerThread triggerThread;
    // 事务处理器
    private MessageHandler workHandler = null;
    // 上报客户端
    private UploadScheduleClient client;

    // 给消息处理需要提供的行为
    private final MessageStrategy.OnMessageBehavior behavior = new MessageStrategy.OnMessageBehavior() {
        @Override
        public MessageHandler loadMessageHandler() {
            return workHandler;
        }

        @Override
        public EventQueue loadEventQueue() {
            return client.getQueue();
        }

        @Override
        public AbstractProcess loadAbstractProcess() {
            return client.getProcess();
        }
    };


    public AsyncTrigger() {
        this(AsyncTrigger.class.getName());
    }

    /**
     * 异步触发器
     * @param name HandlerThread 添加的名称
     */
    public AsyncTrigger(String name) {
        // 创建一个HandlerThread 用于执行消息Loop
        triggerThread = new HandlerThread(name);
        triggerThread.start();

        // 创建绑定在triggerThread的handler
        workHandler = new MessageHandler(triggerThread.getLooper(), msg -> {
            // 获取并执行消息策略
            MessageStrategy messageStrategy = DefaultMessageGroup.obtainByStatus(msg.what);
            if (messageStrategy != null) {
                messageStrategy.handleEvent(behavior);
                return true;
            }

            // 一般不会执行到当前项，若执行到，重试一次
            if (msg.what != DefaultMessageGroup.TYPE_COMPLETE && workHandler != null) {
                workHandler.handleError();
            }
            return true;
        });
    }

    /**
     * 附加数据上报客户端 主要为了拿到消息队列 和 执行流程策略
     *
     * @param client 数据上报客户端
     */
    public void attach(UploadScheduleClient client) {
        this.client = client;
    }


    /**
     * 获取绑定在triggerThread 的消息执行器
     */
    MessageHandler getWorkHandler() {
        return workHandler;
    }

    /**
     * 销毁
     */
    void onDestroy() {
        if (triggerThread != null) {
            triggerThread.quitSafely();
        }
    }
}
