package com.flying.cattle.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestParallelGateWay {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署流程使用classPath
     */
    @Test
    public void deployProcess1() {
        String bpmnName = "ParallelGateWay";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("购物流程");
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
     * 启动流程：
     *
     * 此时act_ru_execution3条数据，都是一个流程实例ID，并行网关现在有两个执行实例，整个是一个流程实例
     * 两个执行实例(parent_id不为null)属于流程实例,执行实例必须归属某个流程实例
     * 流程定义ID一致
     * 执行实例id和流程实例id一致时(单线流程)
     *
     * 历史流程实例只有1条数据即该流程实例
     * act_ru_task此时 2条数据
     * 历史任务实例act_hi_taskinst 2条数据
     * 历史活动结点act_hi_actinst 4条数据
     *
     * 发货任务执行后：
     *
     * act_ru_task此时 2条数据
     * 历史任务实例act_hi_taskinst 3条数据
     * 历史活动结点act_hi_actinst 5条数据
     *
     *
     * 收货任务执行后：
     *
     * act_ru_task此时 1条数据
     * 历史任务实例act_hi_taskinst 3条数据
     * 历史活动结点act_hi_actinst 6条数据
     *
     *
     * 付款任务执行后：
     *
     * act_ru_task此时 1条数据
     * 历史任务实例act_hi_taskinst 4条数据
     * 历史活动结点act_hi_actinst 7条数据
     *
     * 收款任务执行后：
     *
     * act_ru_task此时 0条数据
     * 历史任务实例act_hi_taskinst 4条数据
     * 历史活动结点act_hi_actinst 9条数据
     *
     * 执行实例重合之后才能完成
     */
    @Test
    public void startProcess() {
        String processDfinationKey = "ParallelGateWay";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }

    /**
     * 查询我的个人任务act_ru_task
     */
    @Test
    public void queryMyTask() {
        String assignee = "商家";
        List<Task> list = taskService.createTaskQuery()
                //条件
                .taskAssignee(assignee)//根据任务办理人查询任务
                //排序
                .orderByTaskCreateTime().desc()
                //结果集
                .list();
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
    public void completeTaskNoVariables() {
        //查询任务的任务id
        String taskId = "dc1a326a-d237-11ea-9b01-d0c637ab19a3";
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

}
