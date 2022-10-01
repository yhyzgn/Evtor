package com.yhy.evtor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件注册器
 * <p>
 * Created on 2022-10-01 22:50
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Registry {
    final static Registry instance = new Registry() {
    };

    private final Map<Object, List<Observer>> targetObserverListMap = new HashMap<>();
    private final Map<String, List<Observer>> nameObserverListMap = new HashMap<>();
    private final Map<String, List<Observer>> nameBroadcastListMap = new HashMap<>();

    private Registry() {
    }

    synchronized void register(Observer observer) {
        List<Observer> observerList = targetObserverListMap.get(observer.getTarget());
        if (null == observerList) {
            observerList = new ArrayList<>();
        }
        observerList.add(observer);

        List<Observer> targetList = nameObserverListMap.get(observer.getName());
        if (null == targetList) {
            targetList = new ArrayList<>();
        }
        targetList.add(observer);

        targetObserverListMap.put(observer.getTarget(), observerList);
        nameObserverListMap.put(observer.getName(), targetList);

        if (observer.isBroadcast()) {
            List<Observer> broadcastList = nameBroadcastListMap.get(observer.getName());
            if (null == broadcastList) {
                broadcastList = new ArrayList<>();
            }
            broadcastList.add(observer);
            nameBroadcastListMap.put(observer.getName(), broadcastList);
        }
    }

    synchronized void unregister(Object target) {
        List<Observer> observerList = targetObserverListMap.get(target);
        if (null != observerList && !observerList.isEmpty()) {
            observerList.removeIf(it -> {
                boolean should = it.getTarget() == target;
                if (should) {
                    // 移除 name -> target 关联
                    List<Observer> targetList = nameObserverListMap.get(it.getName());
                    if (null != targetList) {
                        targetList.removeIf(tl -> tl.getTarget() == target);
                    }
                    if (null == targetList || targetList.isEmpty()) {
                        nameObserverListMap.remove(it.getName());
                    } else {
                        nameObserverListMap.put(it.getName(), targetList);
                    }

                    // 移除广播
                    if (it.isBroadcast()) {
                        List<Observer> broadcastList = nameBroadcastListMap.get(it.getName());
                        if (null != broadcastList) {
                            broadcastList.removeIf(tl -> tl.getTarget() == target);
                        }
                        if (null == broadcastList || broadcastList.isEmpty()) {
                            nameBroadcastListMap.remove(it.getName());
                        } else {
                            nameBroadcastListMap.put(it.getName(), broadcastList);
                        }
                    }
                }
                return should;
            });
        }
        if (null == observerList || observerList.isEmpty()) {
            targetObserverListMap.remove(target);
        } else {
            targetObserverListMap.put(target, observerList);
        }
    }

    List<Observer> observerList(String name) {
        return nameObserverListMap.get(name);
    }

    List<Observer> broadcastList() {
        return nameBroadcastListMap.entrySet().stream().flatMap(it -> it.getValue().stream()).collect(Collectors.toList());
    }
}
