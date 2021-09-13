package org.sheedon.uploader.process;

import org.sheedon.uploader.EventListener;
import org.sheedon.uploader.EventQueue;
import org.sheedon.uploader.MessageHandler;

/**
 * 执行真实业务的调度流程，拿到EventQueue中的事件监听器EventListener，
 * 调用执行动作handleEvent，发起真实处理。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:54 下午
 */
public class ScheduleProcess extends AbstractProcess {

    // 消息队列
    private final EventQueue queue;
    // 消息处理器
    private final MessageHandler workHandler;

    public ScheduleProcess(EventQueue queue, MessageHandler workHandler) {
        this.queue = queue;
        this.workHandler = workHandler;
    }

    /**
     * 执行任务，拿到事件监听器，并且执行事件
     * 若拿不到事件，则调度失败，一般在于「子调度器」主动移除任务
     *
     * @return 是否可以调度事件
     */
    @Override
    public boolean execute() {
        EventListener listener = queue.getEventListenerByKey();
        if (listener == null) {
            return false;
        }
        listener.handleEvent(workHandler);
        return true;
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        if (nextProcess != null) {
            nextProcess.onDestroy();
        }
    }
}
