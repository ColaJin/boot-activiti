package com.flying.cattle.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 接收活动
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestReceiveTask {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 部署流程使用classPath,部署时不会发生覆盖
     */
    @Test
    public void deployProcess1() {
        String bpmnName = "ReceiveTask";
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
     * 此时act_ru_task 0条数据
     * 不属于任务
     */
    @Test
    public void startProcess() {
        String processDfinationKey = "ReceiveTask";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }


    @Test
    public void executtionTask() {
        String processInstanceId = "1a2461ba-d2ef-11ea-b38f-d0c637ab19a3";
        //查询执行对象ID
        Execution execution = runtimeService.createExecutionQuery()//创建执行对象查询
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .activityId("receiveTask1")//当前活动的id，对应receiveTask.bpmn文件中的活动结点id的属性值
                .singleResult();
        System.out.println("执行实例ID:" + execution.getId());
        System.out.println("流程实例ID:" + execution.getProcessInstanceId());
        System.out.println("活动ID:" + execution.getActivityId());
        //使用流程变量设置当日销售额，用来传递业务参数
        int value = 10000;//应该是去查询数据库，进行汇总--------耗时操作
        runtimeService.setVariable(execution.getId(), "当前的销售额", value);

        //向后执行一步，如果流程处于等待状态，使得流程继续执行
        runtimeService.trigger(execution.getId());
    }

    /**
     * 发短信
     */
    @Test
    public void sendMessage() {
        String executionId = "1a2488cb-d2ef-11ea-b38f-d0c637ab19a3";
        //从流程变量中获取汇总当日销售额的值
        Integer value = (Integer) runtimeService.getVariable(executionId, "当前的销售额");
        System.out.println(value);
        System.out.println("发送短信");//--------耗时操作
        Boolean flag = false;
        int num = 0;
        do {
            //至少执行一次
            flag = send();
            num++;
            if (num == 10) {
                System.out.println("尝试10次发送，全部失败，已终止发送");//错误日志
                break;
            }
        } while (!flag);
        //向后执行一步，如果流程处于等待状态，使得流程继续执行
        runtimeService.trigger(executionId);
        System.out.println("流程执行完成");

    }

    private Boolean send() {
        System.out.println("发送成功");
        return true;
    }

    @Test
    public void doTask() {
        String processDfinationKey = "ExclusiveGateWay";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey);//根据流程定义的key启动流程
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
        //查询执行对象ID
        Execution execution = runtimeService.createExecutionQuery()//创建执行对象查询
                .processInstanceId(processInstance.getId())//使用流程实例ID查询
                .activityId("receiveTask1")//当前活动的id，对应receiveTask.bpmn文件中的活动结点id的属性值
                .singleResult();
        //使用流程变量设置当日销售额，用来传递业务参数
        int value = 0;//应该是去查询数据库，进行汇总--------耗时操作
        int tryNum = 0;
        while (true) {
            tryNum++;
            try {
                value = hzxx();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (tryNum == 10) {
                    System.out.println("尝试10次汇总，全部失败，已终止汇总");
                    break;
                }
            }
        }
        runtimeService.setVariable(execution.getId(), "当前的销售额", value);

        //向后执行一步，如果流程处于等待状态，使得流程继续执行
        runtimeService.trigger(execution.getId());

        //从流程变量中获取汇总当日销售额的值
        Integer salemoney = (Integer) runtimeService.getVariable(execution.getId(), "当前的销售额");
        System.out.println(salemoney);
        System.out.println("发送短信");//--------耗时操作
        Boolean flag = false;
        int num = 0;
        do {
            //至少执行一次
            flag = send();
            num++;
            if (num == 10) {
                System.out.println("尝试10次发送，全部失败，已终止发送");//错误日志
                break;
            }
        } while (!flag);
        //向后执行一步，如果流程处于等待状态，使得流程继续执行
        runtimeService.trigger(execution.getId());
        System.out.println("流程执行完成");
    }

    public Integer hzxx() {
        //查询数据库
        System.out.println("数据汇总中.....");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据汇总完成");
        return 10000;
    }


}
