package org.sheedon.uploader.process;

/**
 * 抽象流程，链式调用，依次执行当前任务后，在调度执行下一个任务
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/11 11:22 下午
 */
public abstract class AbstractProcess {

    // 需要调度的下一个流程
    protected AbstractProcess nextProcess;

    /**
     * 执行下一个流程
     *
     * @param nextProcess 下一个流程
     */
    public void setNextProcess(AbstractProcess nextProcess) {
        this.nextProcess = nextProcess;
    }

    /**
     * 执行任务，并且返回当前执行是否成功
     */
    public abstract boolean execute();

    /**
     * 销毁
     */
    public abstract void onDestroy();
}
