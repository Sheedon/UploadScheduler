package org.sheedon.uploader.message;

import android.util.SparseArray;

import androidx.annotation.IntDef;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 默认消息策略组
 * 默认实现的7种策略，使用过程中，基本采用以下7种就足够了
 * 0. 默认
 * 1. 核实
 * 2. 执行中
 * 3. 执行完成
 * 4. 执行失败
 * 5. 执行延期处理
 * 6. 超时
 * 7. 销毁
 * <p>
 * 用于根据类型创建消息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:11 上午
 */
public final class DefaultMessageGroup {

    // 默认状态
    public static final int TYPE_NORMAL = 0;
    // 核实状态
    public static final int TYPE_CHECK = 1;
    // 执行状态
    public static final int TYPE_EXECUTE = 2;
    // 调度完成状态
    public static final int TYPE_COMPLETE = 3;
    // 调度失败状态
    public static final int TYPE_FAILURE = 4;
    // 等待延迟调度状态
    public static final int TYPE_DELAY = 5;
    // 超时
    public static final int TYPE_TIMEOUT = 6;
    // 销毁状态
    public static final int TYPE_DESTROY = 7;

    // 超时时间
    public static final int TIMEOUT = 30000;

    @IntDef({TYPE_NORMAL, TYPE_CHECK, TYPE_EXECUTE, TYPE_COMPLETE, TYPE_FAILURE, TYPE_DELAY, TYPE_TIMEOUT, TYPE_DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusType {
    }

    private final static SparseArray<MessageStrategy> sparseArray = new SparseArray<MessageStrategy>() {
        {
            this.put(TYPE_NORMAL, new NormalMessage());
            this.put(TYPE_CHECK, new CheckMessage());
            this.put(TYPE_EXECUTE, new ExecuteMessage());
            this.put(TYPE_COMPLETE, new CompleteMessage());
            this.put(TYPE_FAILURE, new FailureMessage());
            this.put(TYPE_DELAY, new DelayMessage());
            this.put(TYPE_TIMEOUT, new TimeOutMessage());
            this.put(TYPE_DESTROY, new DestroyMessage());
        }
    };


    /**
     * 将类型转 MessageStrategy 返回
     *
     * @param status 消息类型
     * @return MessageStrategy 消息策略
     */
    public static MessageStrategy obtainByStatus(@StatusType int status) {
        return sparseArray.get(status);
    }

}
