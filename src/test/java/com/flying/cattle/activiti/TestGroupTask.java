package com.flying.cattle.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
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
 * 组任务
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestGroupTask {

    //第一种方式：直接指定办理人，即画图时指定Candidate Users，缺点办理人固定，实际开发中办理人不固定，注意绘图不要重复操作

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
        String bpmnName = "GroupTaskThree";
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
     */
    @Test
    public void startProcess() {
        String processDfinationKey = "GroupTaskThree";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }

    /**
     * 查询组任务，act_ru_task的Asignee为null，不能使用代理人方式查询，因为代理人现在为null，组任务拾取后变成个人任务
     * 第一种方式此时出现问题查询不到申请者的任务
     */
    @Test
    public void queryMyTask() {
        String candidateUser = "小A";
        List<Task> list = taskService.createTaskQuery()
                //条件
                .taskCandidateUser(candidateUser)
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
     * 拾取后根据代理人查询
     */
    @Test
    public void queryMyTaskByAsignee() {
        String assignee = "小A";
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
     * 任务拾取目的把组任务变成个人任务
     */
    @Test
    public void claim() {
        String taskId = "effe92c2-d5fa-11ea-a03d-d0c637ab19a3";
        taskService.claim(taskId, "小A");
        System.out.println("任务拾取成功");
    }

    /**
     * 任务回退，只有组任务可以回退
     */
    @Test
    public void clainBack() {
        String taskId = "ea25222d-d5f4-11ea-94e6-d0c637ab19a3";
        taskService.setAssignee(taskId, null);
        System.out.println("任务回退成功");
    }

    /**
     * 查询组任务成员列表,任务拾取后，查询组任务增加Type类型是Asignee的任务成员列表
     */
    @Test
    public void findGroupUser() {
        String taskId = "dc6eec5d-d60c-11ea-9db8-d0c637ab19a3";
        List<IdentityLink> list = taskService.getIdentityLinksForTask(taskId);

        for (IdentityLink identityLink : list) {
            System.out.println("userId="+identityLink.getUserId());
            System.out.println("taskId="+identityLink.getUserId());
            System.out.println("piId="+identityLink.getUserId());
            System.out.println("Type="+identityLink.getType());
            System.out.println("*************************************");
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
        variables.put("username", "李四");
        taskService.complete(taskId, variables);
        System.out.println("任务完成");
    }

    //********************************************************************
    /**
     * 方式二
     * 启动流程,使用流程变量启动,流程图添加Candidate Users的#{usernames}
     */
    @Test
    public void startProcessTwo() {
        String processDfinationKey = "GroupTaskTwo";
        Map<String, Object> variables = new HashMap<>();
        variables.put("usernames","小A,小B,小C,小D");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey,variables);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }
    //********************************************************************
    /**
     * 方式三
     * 流程图取消Candidate Users，增加监听类时想删除点击文件夹，启动不需要流程变量
     */


    /**
     * 添加组任务成员
     */
    @Test
    public void addGroupUser() {
        String taskId = "0d4113dd-d614-11ea-b68c-d0c637ab19a3";
        taskService.addCandidateUser(taskId,"小E");
        System.out.println("添加组成员成功");
    }

    /**
     * 删除组任务成员，只能删除candidate类型数据，小E不能拾取但是参与过该组任务
     */
    @Test
    public void deleteGroupUser() {
        String taskId = "0d4113dd-d614-11ea-b68c-d0c637ab19a3";
        taskService.deleteCandidateUser(taskId,"小E");
    }
}
