package com.flying.cattle.activiti;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flying.cattle.activiti.entity.User;
import io.swagger.models.auth.In;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程变量测试
 * <p>
 * 相关表
 * <p>
 * act_ru_variable
 * act_hi_varinst
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestProcessVariables {

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
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("测试流程");
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
     * 启动流程并设置流程变量
     */
    @Test
    public void startProcess() {
        String processDfinationKey = "JYDProcess";
        //创建流程变量对象
        Map<String, Object> variables = new HashMap<>();
        variables.put("请假天数", 5);
        variables.put("请假原因", "约会");
        variables.put("请假时间", new Date());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDfinationKey, variables);
        System.out.println("流程启动成功:" + processInstance.getId() + "     " + processInstance.getProcessDefinitionId() + "     " + processInstance.getProcessDefinitionKey() + "    " + processInstance.getProcessInstanceId());
    }

    /**
     * 设置流程变量，使用runtimeService
     */
    @Test
    public void setVariables1() {
        String executionId = "0bc16e5f-d156-11ea-9046-d0c637ab19a3";
        /*runtimeService.setVariable(executionId,"请假人","小明");
        System.out.println("流程变量设置成功");*/
        //key不改变，只是value发生变化，所有数据数量不发生变化,value值发生变化饿数据REV_字段值加一(相当于版本)
        Map<String, Object> variables = new HashMap<>();
        variables.put("请假天数", 6);
        variables.put("请假原因", "约会男票");
        variables.put("请假时间", new Date());
        //此处存在问题：User实现了Seriable类但是type类型还是json不是Seriable，BYTEARRAY_ID_应该有值，act_ge_bytearray增加两条数据执行是和历史的用户信息
        variables.put("用户对象S", new User(1, "小明"));
        runtimeService.setVariables(executionId, variables);
        System.out.println("流程变量设置成功");
    }

    /**
     * 设置流程变量，使用taskService
     */
    @Test
    public void setVariables2() {
        String taskId = "0bc60242-d156-11ea-9046-d0c637ab19a3";
        Map<String, Object> variables = new HashMap<>();
        variables.put("任务设置的id", 9);
        //taskService.setVariable(taskId,"任务配置设置variable","variable");
        taskService.setVariables(taskId, variables);
        System.out.println("流程变量设置成功");
    }


    /**
     * 获取流程变量
     */
    @Test
    public void getVariables() throws IOException {
        String executionId = "0bc16e5f-d156-11ea-9046-d0c637ab19a3";
        Integer days = (Integer) runtimeService.getVariable(executionId, "请假天数");
        Date date = (Date) runtimeService.getVariable(executionId, "请假时间");
        //可以type强制转换成String，但是是json不能强制转换成String，可以直接使用toString()方法然后转换成java对象
        String result = runtimeService.getVariable(executionId, "用户对象").toString();
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(result, User.class);
        System.out.println(days);
        System.out.println(DateFormat.getDateTimeInstance().format(date));
        System.out.println(user.getId() + "   " + user.getName());
    }

    /**
     * 查询历史流程变量
     */
    @Test
    public void getHistoryVariables() {
        //获取某一个历史流程变量
        /*HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().id("159f36ec-d161-11ea-a2e5-d0c637ab19a3").singleResult();
        System.out.println(historicVariableInstance.getId());
        System.out.println(historicVariableInstance.getValue());
        System.out.println(historicVariableInstance.getVariableName());
        System.out.println(historicVariableInstance.getVariableTypeName());*/

        //获取某一个流程下的所有历史流程变量
        String processInstanceId = "0bc05ce8-d156-11ea-9046-d0c637ab19a3";
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        if (!CollectionUtils.isEmpty(list)) {

            for (HistoricVariableInstance hv : list) {
                System.out.println("ID" + hv.getId());
                System.out.println("变量值" + hv.getValue());
                System.out.println("变量名" + hv.getVariableName());
                System.out.println("变量类型" + hv.getVariableTypeName());
                System.out.println("*******************************");
            }
        }
    }


    /**
     * 设置流程变量，使用runtimeService.setVariablesLocal
     */
    @Test
    public void setVariables3() {
        String executionId = "0bc16e5f-d156-11ea-9046-d0c637ab19a3";
        Map<String, Object> variables = new HashMap<>();
        variables.put("测试", "setVariablesLocal");
        //流程存在分支时，PROC_INST_ID会发生变化
        runtimeService.setVariablesLocal(executionId, variables);
        System.out.println("流程变量设置成功");
    }

    /**
     * 设置流程变量，使用taskService.setVariablesLocal
     */
    @Test
    public void setVariables4() {
        String taskId = "0bc60242-d156-11ea-9046-d0c637ab19a3";
        Map<String, Object> variables = new HashMap<>();
        variables.put("测试2", 9);
        //绑定测试ID
        taskService.setVariablesLocal(taskId, variables);
        System.out.println("流程变量设置成功");
    }

}
