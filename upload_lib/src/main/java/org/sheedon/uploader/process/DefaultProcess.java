package org.sheedon.uploader.process;

import android.content.Context;

import org.sheedon.uploader.EventQueue;
import org.sheedon.uploader.INetConnected;
import org.sheedon.uploader.MessageHandler;

/**
 * 默认流程处理者，捆绑并且依次调用「网络核实流程 NetCheckProcess」、「服务器连接流程 ServiceCheckProcess」
 * 和「事件真实执行流程 ScheduleProcess」。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:59 下午
 */
public class DefaultProcess extends AbstractProcess {

    // 网络核实流程，用于流程处理根节点
    private final NetCheckProcess process;

    /**
     * 默认流程调度器
     *
     * @param context     上下文
     * @param baseUrl     基础服务器链接
     * @param queue       事件队列
     * @param workHandler 消息工作执行器
     */
    public DefaultProcess(Context context, String baseUrl, INetConnected netConnected, EventQueue queue, MessageHandler workHandler) {
        process = new NetCheckProcess(context);
        ServiceCheckProcess serviceCheckProcess = new ServiceCheckProcess(baseUrl, netConnected);
        ScheduleProcess scheduleProcess = new ScheduleProcess(queue, workHandler);


        process.setNextProcess(serviceCheckProcess);
        serviceCheckProcess.setNextProcess(scheduleProcess);
    }

    /**
     * 执行任务
     */
    @Override
    public boolean execute() {
        return process.execute();
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        if (process != null) {
            process.onDestroy();
        }
    }
}
