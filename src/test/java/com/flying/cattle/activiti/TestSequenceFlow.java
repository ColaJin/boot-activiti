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
public class TestSequenceFlow {

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
        String bpmnName = "SequenceFlow";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("报销流程");
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
     * 启动流程，bpmn文件出现问题需要重新启动流程，否则二进制文件不更新，导致运行还是存在问题
     */
    @Test
    public void startProcess() {
        String processDfinationKey = "SequenceFlow";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }

    /**
     * 查询我的个人任务act_ru_task
     */
    @Test
    public void queryMyTask() {
        String assignee = "李四";
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
     * 办理任务并流程变量指定流程走向
     */
    @Test
    public void completeTaskNoVariables() {
        //查询任务的任务id
        String taskId = "c3759cda-d221-11ea-955d-d0c637ab19a3";
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

    /**
     * 办理任务并流程变量指定流程走向
     */
    @Test
    public void completeTask() {
        //查询任务的任务id
        String taskId = "69e31f5d-d220-11ea-b2ae-d0c637ab19a3";
        //根据任务ID去完成任务并指定流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("outcome","重要");
        taskService.complete(taskId,variables);
        System.out.println("任务完成");
    }
}
