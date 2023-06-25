package org.sheedon.uploader;

import android.content.Context;

import androidx.annotation.NonNull;

import org.sheedon.uploader.process.AbstractProcess;
import org.sheedon.uploader.process.DefaultProcess;

/**
 * 数据上报/数据处理 执行客户端，建议单例维持。
 * 由外界增加任务时「submitEvent()」，启动队列任务，
 * 再借助消息发送器{@link MessageHandler} 从消息队列中取出消息策略{@link org.sheedon.uploader.message.MessageStrategy}
 * 并发予{@link AsyncTrigger}依次执行任务。
 * <p>
 * 注意：{@link AsyncTrigger} 和 @{@link MessageHandler}并不是任务执行者，而是任务的调度者，
 * 这两者只是生产各种消息策略，将任务如永动机般轮询。
 * 真实启动任务由{@link AbstractProcess}，当前主要为了上报离线记录，故采用的策略为「网络」+「服务器」+「真实调度提交任务」，
 * 可自定义实现。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/11 11:19 下午
 */
public class UploadScheduleClient {
    // 流程
    private final AbstractProcess process;
    // 资源链接
    private final EventQueue queue;
    // 消息处理器
    private final MessageHandler handler;
    // 异步处理器
    private final AsyncTrigger trigger;

    private UploadScheduleClient(Builder builder) {
        process = builder.process;
        queue = builder.queue;
        trigger = builder.trigger;
        handler = trigger.getWorkHandler();
        trigger.attach(this);
    }

    /**
     * 提交事件，等待执行
     * 业务处理客户端，主要借助与此方法，将代表自身的key（类名）和EventListener事件监听器，传递进来，
     * 由当前模块达到符合条件后调度
     *
     * @param key      类名
     * @param listener 事件监听器
     */
    public void submitEvent(String key, EventListener listener) {
        queue.offerEvent(key, listener);
        handler.sendDelayMessage();
    }

    /**
     * 移除事件，业务处理客户端不需要当前模块处理任务后，可手动解除事件绑定
     *
     * @param key 类名
     */
    public void removeEvent(String key) {
        queue.deleteEvent(key);
    }

    /**
     * 消息队列
     */
    EventQueue getQueue() {
        return queue;
    }

    /**
     * 数据处理流程
     */
    AbstractProcess getProcess() {
        return process;
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (process != null) {
            process.onDestroy();
        }
        if (trigger != null) {
            trigger.onDestroy();
        }
        if (queue != null) {
            queue.onDestroy();
        }
    }

    /**
     * 数据上报执行构造器
     */
    public static class Builder {

        private final Context context;
        // 请求执行流程
        private AbstractProcess process;
        // 资源链接
        private final EventQueue queue;
        // 异步处理调度器
        private final AsyncTrigger trigger;
        // 服务器链接url
        private String baseUrl;

        private INetConnected netConnected;

        public Builder(Context context) {
            this.context = context;
            queue = new EventQueue();
            trigger = new AsyncTrigger();
        }

        /**
         * 添加数据处理执行的流程（责任链）
         *
         * @param process 执行流程策略
         * @return Builder 构造器
         */
        public Builder process(@NonNull AbstractProcess process) {
            this.process = process;
            return this;
        }

        /**
         * 添加网络连接状态
         *
         * @param netConnected 网络连接状态监听器
         * @return Builder 构造器
         */
        public Builder netConnected(@NonNull INetConnected netConnected) {
            this.netConnected = netConnected;
            return this;
        }

        /**
         * 添加服务器核实的链接
         *
         * @param baseUrl Api 基础URL
         * @return Builder 构造器
         */
        public Builder baseUrl(@NonNull String baseUrl) {
            if (baseUrl.trim().isEmpty()) throw new NullPointerException("baseUrl is null");
            this.baseUrl = baseUrl;
            return this;
        }


        /**
         * 创建数据上报执行客户端
         * 验证 必须添加 「自定义流程处理或url」任意一项
         * 若 process 未自定义，则构建默认流程调度器
         *
         * @return UploadScheduleClient
         */
        public UploadScheduleClient build() {

            if (process == null && netConnected == null && (baseUrl == null || baseUrl.trim().isEmpty())) {
                throw new NullPointerException("please add AbstractProcess or baseUrl");
            }

            if (process == null) {
                process = new DefaultProcess(context, baseUrl, netConnected, queue, trigger.getWorkHandler());
            }

            return new UploadScheduleClient(this);
        }
    }
}
