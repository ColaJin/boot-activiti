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

/**
 * 个人任务
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPersonTask {

    //第一种方式：直接指定办理人，即画图时指定Assignee，缺点办理人固定，实际开发中办理人不固定

    //第二种方式：使用流程变量

    //第三种方式：使用类+流程变量，即创建监听类，当任务到达相应结点时会触发监听，让监听器去制定下一个任务的办理人，启动流程仍然使用流程变量
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署流程使用classPath,部署时不会发生覆盖
     */
    @Test
    public void deployProcess1() {
        String bpmnName = "PersonTaskTypeTwo";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("发送流程");
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
     * 并且制定下一任务办理人，否则报错
     */
    @Test
    public void startProcess() {
        String processDfinationKey = "PersonTaskTypeTwo";
        Map<String, Object> variables=new HashMap<>();
        variables.put("username","张三");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey,variables);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }

    /**
     * 查询我的个人任务act_ru_task
     */
    @Test
    public void queryMyTask() {
        String assignee = "张三";
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
     * 直接办理可以成功完成任务，但是下一任务的办理人还是第一个任务结点的办理人，出现问题
     */
    @Test
    public void completeTaskNoVariables() {
        //查询任务的任务id
        String taskId = "79ad0a98-d2f7-11ea-ab36-d0c637ab19a3";
        /*//设置代理人，未使用指定代理人和监听器时可使用
        taskService.setAssignee(taskId,"李四");*/
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

    /**
     * 办理任务并流程变量设置办理人解决下一任务的办理人问题
     */
    @Test
    public void completeTask() {
        //查询任务的任务id
        String taskId = "dc1a326a-d237-11ea-9b01-d0c637ab19a3";
        //根据任务ID去完成任务并指定流程变量
        Map<String, Object> variables = new HashMap<>();
        //最后一次不需要设置办理人，存在问题，解决方式，根据当前办理用户查询其领导，然后设置领导的名称
        //请假流程对领导的限制没有的，也就是顶层没有请假的申请菜单，就是说如文档接收活动图一
        variables.put("username","李四");
        taskService.complete(taskId,variables);
        System.out.println("任务完成");
    }


}
