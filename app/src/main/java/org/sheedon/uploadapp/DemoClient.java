package org.sheedon.uploadapp;


/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 6:11 下午
 */
public class DemoClient {

    private final static DemoClient INSTANCE = new DemoClient();

    private final UserHandler handler;

    public static DemoClient getInstance() {
        return INSTANCE;
    }

    private DemoClient() {
        handler = new UserHandler();
    }

    public void initConfig() {
        handler.startUp();
    }

    /**
     * 新增消息
     */
    public void addMessage() {
        handler.notifyNewSource();
    }


}
