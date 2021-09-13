package org.sheedon.uploadapp;

import org.sheedon.uploader.AbstractRealHandler;
import org.sheedon.uploader.MessageHandleCenter;
import org.sheedon.uploader.UploadScheduleClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/13 6:36 下午
 */
public class UserHandler extends AbstractRealHandler<UserModel> {

    // 单核线程池
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected UploadScheduleClient loadScheduleClient() {
        return UploadHelper.getClient();
    }

    private boolean isFirst = true;

    @Override
    protected void loadSource(OnSourceCallback<UserModel> callback) {
        // 模拟数据库取数据
        executor.execute(() -> {
            // 模拟耗时操作
            sleep(1);

            List<UserModel> users = new ArrayList<>();
            if (isFirst) {
                isFirst = false;
                for (int index = 0; index < 5; index++) {
                    users.add(new UserModel());
                }
            }
            callback.attachSource(users);
        });
    }

    @Override
    protected void handleRealEvent(UserModel userModel, MessageHandleCenter center) {
        // 模拟网络请求
        executor.execute(() -> {
            sleep(2);
            if (new Random().nextInt(300)<260) {
                center.sendSuccessMessage();
            } else {
                center.sendFailureMessage();
            }
        });
    }

    private void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
