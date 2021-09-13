package org.sheedon.uploadapp;

import org.sheedon.uploader.UploadScheduleClient;

/**
 * 上传辅助类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 6:11 下午
 */
public class UploadHelper {

    private static final UploadHelper instance;
    private UploadScheduleClient client;

    static {
        instance = new UploadHelper();
    }

    private UploadHelper() {
    }

    public static UploadScheduleClient getClient() {
        if (instance.client != null) {
            return instance.client;
        }

        instance.client = new UploadScheduleClient.Builder(App.getInstance())
                .baseUrl("https://www.baidu.com")
                .build();

        return instance.client;
    }
}
