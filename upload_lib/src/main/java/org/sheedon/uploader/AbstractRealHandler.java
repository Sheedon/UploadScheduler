package org.sheedon.uploader;

import java.util.ArrayList;
import java.util.List;

/**
 * 各项业务需要实现的基础处理逻辑
 * 包含流程为：
 * 启动 -> 取（数据库或其他方式）资源 -> 是否有资源 -> 无 则结束
 * -> 有数据，清空缓存，将新获取数据填充到缓存中 -> 提交调度任务（至当前库{@link UploadScheduleClient.submitEvent()}）
 * -> 等待调度
 * -> 执行调度，发送真实操作 -> 失败，反馈失败结果（回到「等待调度」）
 * -> 成功调度，缓存中移除记录
 * -> 核实缓存是否有数据 -> 有，等待调度
 * -> 无 「取（数据库或其他方式）资源」
 * <p>
 * 监听到新消息
 * -> 「核实缓存是否有数据」-> ...
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/13 4:44 下午
 */
public abstract class AbstractRealHandler<Source> {

    // 资源缓存
    private final List<Source> sources = new ArrayList<>();
    // 数据上传执行器
    private final UploadScheduleClient scheduleClient;
    // 消息执行器
    private MessageHandleCenter messageHandleCenter;

    /**
     * 事件监听器，用于核实是否有资源，以及处理真实调度
     */
    private final EventListener eventListener = new EventListener() {

        /**
         * 子调度器是否还有资源需要调度
         */
        @Override
        public boolean hasSource() {
            return !sources.isEmpty();
        }

        /**
         * 执行调度，数据上报操作
         * @param center 消息执行，发送成功/失败
         */
        @Override
        public void handleEvent(MessageHandleCenter center) {
            if (sources.isEmpty()) {
                center.sendFailureMessage();
                return;
            }

            // 代理消息执行，回调后需要做些额外的处理行为
            messageHandleCenter = center;
            Source source = sources.get(0);
            handleRealEvent(source, handleCenter);
        }
    };

    /**
     * 消息发送代理，
     * 发送成功 需要移除当前处理任务，与核实资源
     * 发送失败 反馈失败调度
     */
    private final MessageHandleCenter handleCenter = new MessageHandleCenter() {

        /**
         * 发送成功，先移除，在反馈，最后核实是否还有资源需要上报
         */
        @Override
        public void sendSuccessMessage() {
            if (!sources.isEmpty()) {
                sources.remove(0);
            }

            if (messageHandleCenter != null) {
                messageHandleCenter.sendSuccessMessage();
            }

            if (sources.isEmpty()) {
                loadSourceToSubmit();
            }

        }

        /**
         * 发送失败消息
         */
        @Override
        public void sendFailureMessage() {
            if (needRemoveFirstByFailure()) {
                if (!sources.isEmpty()) {
                    sources.remove(0);
                }
            }
            if (messageHandleCenter != null) {
                messageHandleCenter.sendFailureMessage();
            }
        }
    };


    public AbstractRealHandler() {
        scheduleClient = loadScheduleClient();
    }

    /**
     * 启动,执行取数据库动作，核实资源，填充缓存，触发调度指令
     */
    public void startUp() {
        loadSourceToSubmit();
    }

    /**
     * 加载 数据上传执行器
     * 后续执行 「提交调度任务」和「清除当前绑定」职责
     *
     * @return UploadScheduleClient  数据上传执行器
     */
    protected abstract UploadScheduleClient loadScheduleClient();

    /**
     * 取（数据库或其他方式）资源，无数据则结束
     * 有数据，清空缓存，将新获取数据填充到缓存中
     * 提交调度任务
     */
    private void loadSourceToSubmit() {
        loadSource(sources -> {
            // 执行器不存在,后续无法执行操作，则return，无需消耗获取信息所造成的资源消耗
            if (scheduleClient == null) return;

            this.sources.clear();
            if (sources == null || sources.isEmpty()) {
                scheduleClient.removeEvent(this.getClass().getCanonicalName());
                return;
            }

            this.sources.addAll(sources);
            scheduleClient.submitEvent(this.getClass().getCanonicalName(), eventListener);
        });
    }

    /**
     * 加载真实资源
     *
     * @param callback 获取后反馈
     */
    protected abstract void loadSource(OnSourceCallback<Source> callback);


    /**
     * 通知有新数据
     */
    public void notifyNewSource() {
        if (!sources.isEmpty()) {
            return;
        }
        loadSourceToSubmit();
    }


    /**
     * 执行真实事件
     *
     * @param source 资源
     * @param center 消息执行处理中心
     */
    protected abstract void handleRealEvent(Source source, MessageHandleCenter center);

    protected boolean needRemoveFirstByFailure() {
        return false;
    }


    /**
     * 销毁
     */
    public void destroy() {
        if (scheduleClient != null) {
            scheduleClient.removeEvent(this.getClass().getCanonicalName());
        }
        sources.clear();
    }

    /**
     * 资源加载 回调
     *
     * @param <Source> 资源类型
     */
    public interface OnSourceCallback<Source> {

        /**
         * 附加资源
         *
         * @param sources 资源列表
         */
        void attachSource(List<Source> sources);
    }

}
