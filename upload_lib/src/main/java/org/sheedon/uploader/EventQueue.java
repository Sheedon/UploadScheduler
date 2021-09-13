package org.sheedon.uploader;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件队列，事件准备执行到真实调度存在一定的事件间隔，所以采用键值对的方式维持了事件队列，
 * 防止直接持有，导致外部删除后，内部还持有引用。
 * 遍历操作任务，防止出现一个类型的任务操作完成才能操作其他任务
 * 主要由下面两个结构组成
 * eventKeys：事件引用，
 * eventMap：由eventKeys为Key的事件关联
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/9/12 12:10 下午
 */
public final class EventQueue {

    // 事件Map 类名为键，事件监听器为值
    private final Map<String, EventListener> eventMap = new ConcurrentHashMap<>();
    // 事件键的集合
    private final List<String> eventKeys = new CopyOnWriteArrayList<>();
    // 当前需要操作的事件键
    private String currentKey;

    /**
     * 添加任务，按照键值对将子调度器的绑定添加到eventMap和eventKeys中
     *
     * @param key      自调度器唯一名称，一般用A.class.getCanonicalName()
     * @param listener 消息监听器
     */
    void offerEvent(@NonNull String key, @NonNull EventListener listener) {
        if (key.isEmpty()) {
            return;
        }

        if (eventKeys.contains(key)) {
            return;
        }

        eventMap.put(key, listener);
        eventKeys.add(key);
    }

    /**
     * 取出事件，只是从list中拿到,类似于取到一个引用，并且将该引用冲list中移除，
     * 不至于一直执行同一个子调度器的任务
     * 若取不到，则重新核实eventMap.item.hasSource,不存在移除，存在取出该项
     *
     * @return 若有事件，则返回事件名（引用），否则返回null
     */
    public String pollEvent() {
        // 引用队列中有值，则直接返回
        if (!eventKeys.isEmpty()) {
            return currentKey = eventKeys.remove(0);
        }

        if (eventMap.isEmpty()) {
            return currentKey = null;
        }

        // 核实所有事件是否还有需要上传的资源
        // 若没有 则返回null 否则返回具体的key
        List<String> keys = new ArrayList<>(eventMap.keySet());
        for (String key : keys) {
            EventListener listener = eventMap.get(key);
            if (listener == null) {
                eventMap.remove(key);
                continue;
            }

            if (listener.hasSource()) {
                eventKeys.add(key);
                continue;
            }

            eventMap.remove(key);
        }

        // 再次核实是否有数据
        if (!eventKeys.isEmpty()) {
            return currentKey = eventKeys.remove(0);
        }

        return currentKey = null;
    }

    /**
     * 在当前队列中是否还有事件
     */
    public boolean hasEvent() {
        // 引用队列中有值，则直接返回
        if (!eventKeys.isEmpty()) {
            return true;
        }

        if (eventMap.isEmpty()) {
            return false;
        }

        // 核实所有事件是否还有需要上传的资源
        // 若没有 则返回null 否则返回具体的key
        List<String> keys = new ArrayList<>(eventMap.keySet());
        for (String key : keys) {
            EventListener listener = eventMap.get(key);
            if (listener == null) {
                eventMap.remove(key);
                continue;
            }

            if (listener.hasSource()) {
                return true;
            }

            eventMap.remove(key);
        }

        return false;
    }

    /**
     * 通过key拿到事件监听器
     *
     * @return 事件监听器
     */
    public EventListener getEventListenerByKey() {
        if (currentKey != null) {
            return eventMap.get(currentKey);
        }
        String key = pollEvent();
        if (key != null) {
            return eventMap.get(key);
        }
        return null;
    }

    /**
     * 通过key 彻底删除事件
     *
     * @param key 类名
     */
    void deleteEvent(@NonNull String key) {
        eventKeys.remove(key);
        eventMap.remove(key);
    }

    /**
     * 销毁清空
     */
    void onDestroy() {
        eventMap.clear();
        eventKeys.clear();
    }
}
