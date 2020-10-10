package com.flying.cattle.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 流程实例，任务执行
 */

/**
 * 这两个注释必须有否则注入不成功
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestProcessInatance {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 部署流程使用classPath
     */
    @Test
    public void deployProcess1() {
        String bpmnName = "JYDProcess";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("请假流程");
        Deployment deployment = null;
        try {
            deployment = deploymentBuilder.addClasspathResource("processes/" + bpmnName + ".bpmn")
                    .addClasspathResource("processes/" + bpmnName + ".png").deploy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("部署成功：流程部署ID：" + deployment.getId());
    }

    /**
     * 启动流程
     */
    @Test
    public void startProcess() {
        String processDefintionId = "";
        //runtimeService.startProcessInstanceById(processDefintionId);//根据流程定义ID启动流程
        /**
         * 参数一：流程定义ID
         * 参数二：Map<String,Object>   流程变量
         */
        //runtimeService.startProcessInstanceById(processDefintionId,variables);
        /**
         * 参数一：流程定义ID
         * 参数二：String  把业务ID和流程执行实例相绑定
         */
        //runtimeService.startProcessInstanceById(processDefintionId,businessKey);
        /**
         * 参数一：流程定义ID
         * 参数二：String  把业务ID和流程执行实例相绑定
         * 参数三：Map<String,Object>   流程变量
         */
        //runtimeService.startProcessInstanceById(processDefintionId,businessKey,variables);
        String processDfinationKey = "JYDProcess";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey);//根据流程定义的key启动流程
        /**
         * 参数一：流程定义Key
         * 参数二：Map<String,Object>   流程变量
         */
        //runtimeService.startProcessInstanceByKey(processDfinationKey,variables);
        /**
         * 参数一：流程定义Key
         * 参数二：String  把业务ID和流程执行实例相绑定
         */
        //runtimeService.startProcessInstanceByKey(processDfinationKey,businessKey);
        /**
         * 参数一：流程定义Key
         * 参数二：String  把业务ID和流程执行实例相绑定
         * 参数三：Map<String,Object>   流程变量
         */
        //runtimeService.startProcessInstanceByKey(processDfinationKey,businessKey,variables);

        //实际开发中使用的是：
        //runtimeService.startProcessInstanceByKey(processDfinationKey,businessKey,variables);
        //runtimeService.startProcessInstanceByKey(processDfinationKey,businessKey);

        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }

    /**
     * 查询我的个人任务act_ru_task
     */
    @Test
    public void queryMyTask() {
        String assignee = "jydmom";
        List<Task> list = taskService.createTaskQuery()
                //条件
                .taskAssignee(assignee)//根据任务办理人查询任务
                //.deploymentId(deployment)//根据部署id查询 where id=id
                //.executionId(executionId)//根据部署id集合查询 where id in(1,2,3,4)
                //.processDefinitionId(processDefinitionId)//根据执行实例id
                //.processDefinitionKey(processDefinitionKey)//根据流程定义id
                //.processDefinitionKeyIn(processDefinitionKeys)
                //.processDefinitionKeyLike(processDefinitionKey)
                //.processDefinitionName(processDefinitionName)
                //.processDefinitionNameLike(processDefinitionName)
                //.processInstanceBusinessKey(processInstanceBusinessKey)
                //排序
                .orderByTaskCreateTime().desc()
                //结果集
                .list();
        //.listPage(firstResult，maxResult);//分页查询返回list集合
        //.singleResult();//返回单个对象
        //.count();
        if (!CollectionUtils.isEmpty(list)) {
            for (Task task : list) {
                System.out.println("任务ID" + task.getId());
                System.out.println("任务办理人" + task.getAssignee());
                System.out.println("任务实例ID" + task.getExecutionId());
                System.out.println("任务名称" + task.getName());
                System.out.println("流程定义ID" + task.getProcessDefinitionId());
                System.out.println("流程实例ID" + task.getProcessInstanceId());
                System.out.println("任务创建时间" + task.getCreateTime());
                System.out.println("************************");
            }
        }
    }

    /**
     * 办理任务
     */
    @Test
    public void completeTask() {
        //查询任务的任务id
        String taskId = "6dfbd323-ccad-11ea-9d35-d0c637ab19a3";
        //根据任务ID完成任务
        taskService.complete(taskId);
        //根据任务ID去完成任务并指定流程变量
        //taskService.complete(taskId,variables);
        System.out.println("任务完成");
    }

    /**
     * 判断流程是否结束
     * 作用：更新业务表里面的状态
     */
    @Test
    public void isComplete() {
        //已知流程实例ID
        String processInstanceId = "";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null != processInstance) {
            System.out.println("流程未结束");
        }else {
            System.out.println("流程已结束");
        }

        /*//已知任务ID,根据任务id查询任务实例对象，
        String taskId="";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //从任务实例中取出流程实例ID
        String processInstanceId1 = task.getProcessInstanceId();
        //使用流程实例ID查询流程实例表里面是否有数据
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId1).singleResult();
        if (null != processInstance1) {
            System.out.println("流程未结束");
        }else {
            System.out.println("流程已结束");
        }*/

    }

    /**
     * 查询当前流程实例
     */
    @Test
    public void queryProcessInstance() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        if (!CollectionUtils.isEmpty(list)) {
            for (ProcessInstance pi :list) {
                System.out.println("执行实例ID"+pi.getId());
                System.out.println("流程定义ID"+pi.getProcessDefinitionId());
                System.out.println("流程实例ID"+pi.getProcessInstanceId());
                System.out.println("*********************");
            }
        }
    }

    /**
     * 查询历史任务  act_hi_taskinst
     */
    @Test
    public void queryHistoryTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricTaskInstance hi :list) {
                System.out.println("任务ID" + hi.getId());
                System.out.println("任务办理人" + hi.getAssignee());
                System.out.println("任务实例ID" + hi.getExecutionId());
                System.out.println("任务名称" + hi.getName());
                System.out.println("流程定义ID" + hi.getProcessDefinitionId());
                System.out.println("流程实例ID" + hi.getProcessInstanceId());
                System.out.println("任务创建时间" + hi.getCreateTime());
                System.out.println("任务结束时间" + hi.getEndTime());
                System.out.println("任务创持续时间" + hi.getDurationInMillis());
                System.out.println("************************");
            }
        }
    }

    /**
     * 查询历史流程实例
     */
    @Test
    public void queryHistoryProcessInstance() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricProcessInstance hp :list) {
                System.out.println("执行实例ID"+hp.getId());
                System.out.println("流程定义ID"+hp.getProcessDefinitionId());
                System.out.println("流程启动时间"+hp.getStartTime());
                System.out.println("*********************");
            }
        }
    }

}
