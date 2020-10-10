package com.flying.cattle.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 1、查询历史流程实例
 * 2、查询历史活动
 * 3、查询历史任务
 * 4、查询历史流程变量
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestHistoryQuery {
    @Autowired
    private HistoryService historyService;

    /**
     * 1、查询历史流程实例
     */
    @Test
    public void historyProcessInstance() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                //条件
                //.processDefinitionId(processDefinitionId)
                //.processDefinitionKey(processDefinitionKey)
                //.processDefinitionKeyIn(processDefinitionKeys)
                //.processDefinitionName(processDefinitionName)
                //.processInstanceBusinessKey(processInstanceBusinessKey)
                //.processInstanceId(processInstanceId)
                //.processInstanceIds(processInstanceIds)
                //排序
                //.orderByProcessDefinitionId()
                //.orderByProcessInstanceBusinessKey()
                //.orderByProcessInstanceDuration()
                //.orderByProcessInstanceStartTime()
                //.orderByProcessInstanceId()
                //结果集
                .list();
        //.listPage(firstResult，maxResult);
        //.singleResult();
        //.count();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricProcessInstance hpi : list) {
                System.out.println("历史流程实例ID：" + hpi.getId());
                System.out.println("流程定义ID：" + hpi.getProcessDefinitionId());
                System.out.println("历史流程实例的业务ID：" + hpi.getBusinessKey());
                System.out.println("流程部署ID：" + hpi.getDeploymentId());
                System.out.println("流程定义key：" + hpi.getProcessDefinitionKey());
                System.out.println("开始活动ID：" + hpi.getStartActivityId());
                System.out.println("结束活动ID：" + hpi.getEndActivityId());
                System.out.println("**************************************");
            }
        }
    }

    /**
     * 2、查询历史活动
     */
    @Test
    public void getHistoryAct() {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                //条件
                //.activityId(activityId);
                //.activityInstanceId(activityInstanceId)
                //.activityName(activityName)
                //排序
                //.orderByActivityId()
                //.orderByActivityName()
                //结果集
                .list();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricActivityInstance hai : list) {

                System.out.println("ID:" + hai.getId());
                System.out.println("流程定义ID:" + hai.getProcessDefinitionId());
                System.out.println("流程实例ID:" + hai.getProcessInstanceId());
                System.out.println("执行实例ID:" + hai.getExecutionId());
                System.out.println("活动ID:" + hai.getActivityId());
                System.out.println("任务ID:" + hai.getTaskId());
                System.out.println("活动名称:" + hai.getActivityName());
                System.out.println("活动类型:" + hai.getActivityType());
                System.out.println("任务办理人:" + hai.getAssignee());
                System.out.println("开始时间:" + hai.getStartTime());
                System.out.println("结束时间:" + hai.getEndTime());
                System.out.println("持续时间:" + hai.getDurationInMillis());
                System.out.println("****************");
            }
        }
    }

    /**
     * 3、查询历史任务act_hi_taskinst
     */
    @Test
    public void queryHistoryTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                //条件
                //.deploymentId(deploymentId)
                //.deploymentIdIn(deploymentIdS)
                //.executionId(executionId)
                //.processDefinitionId(processDefinitionId)
                //.processDefinitionKey(processDefinitionKey)
                //.processDefinitionKeyIn(processDefinitionKeys)
                //.processDefinitionKeyLike(processDefinitionKey)//注意processDefinitionKey需要手动加%
                //.processDefinitionName(processDefinitionName)
                //.processDefinitionNameLike(processDefinitionName)
                //排序
                //.orderByTaskDefinitionKey()
                //结果集
                .list();
        //.listPage(firstResult，maxResult);
        //.singleResult();
        //.count();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricTaskInstance task : list) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务办理人:" + task.getAssignee());
                System.out.println("执行实例ID:" + task.getExecutionId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
                System.out.println("流程实例ID:" + task.getProcessInstanceId());
                System.out.println("任务创建时间:" + task.getCreateTime());
                System.out.println("任务结束时间:" + task.getEndTime());
                System.out.println("#################");
            }
        }

    }

    /**
     * 4、查询历史流程变量
     */
}
