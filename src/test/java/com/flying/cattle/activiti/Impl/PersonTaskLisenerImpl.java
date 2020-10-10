package com.flying.cattle.activiti.Impl;

import com.flying.cattle.activiti.SpringContextUtils;
import com.flying.cattle.activiti.entity.User;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;

import java.util.List;

/**
 * 监听器，多个任务采用一个监听器指定办理人存在问题：监听的所有的任务变成同一个办理人
 */
public class PersonTaskLisenerImpl implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        HistoryService historyService = SpringContextUtils.getBean(HistoryService.class);
        System.out.println("个人任务监听启动");
        //解决用一个监听器指定办理人存在问题：
        //使用解耦的方式得到HttpServletRequest 再从request里面得到session
        //从session中获取用户
        //根据用户查询用户的上级领导，指定为办理人
        //指定办理人

        String processInstanceId = delegateTask.getProcessInstanceId();

        //获取上面的多个任务
        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();

        //获取最后面一个任务，即本任务的上一个任务
        HistoricTaskInstance lastTask=taskList.get(0);

        //获取上一个任务的执行人
        String lastTaskAsignee = lastTask.getAssignee();
        // 根据上个任务的办理人查询下个任务的办理人
        String assignee = "李四";
        delegateTask.setAssignee(assignee);
    }
}
