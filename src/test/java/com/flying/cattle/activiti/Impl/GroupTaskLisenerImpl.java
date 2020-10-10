package com.flying.cattle.activiti.Impl;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 监听器，多个任务采用一个监听器指定办理人存在问题：监听的所有的任务变成同一个办理人
 */
public class GroupTaskLisenerImpl implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("组任务监听启动");
        delegateTask.addCandidateUser("小A");
        delegateTask.addCandidateUser("小B");
        delegateTask.addCandidateUser("小C");
        delegateTask.addCandidateUser("小D");
    }
}
