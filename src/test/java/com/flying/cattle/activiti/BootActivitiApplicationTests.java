package com.flying.cattle.activiti;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.flying.cattle.activiti.config.SecurityUtil;
import org.springframework.util.CollectionUtils;

/**
 * activiti7
 * 主要是对以前项目做了一些封装
 * ProcessRuntime taskRuntime:本质还是以前的各种service;
 * 版本模糊，个版本都有一定的bug。团队实力有待考察。
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class BootActivitiApplicationTests {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    //流程图的部署、修改、删除的服务器act_ge_bytearray，act_re_deployment，act_re_model，act_re_procdef
    @Autowired
    private RepositoryService repositoryService;

    //流程的运行act_ru_event_subscr，act_ru_execution，act_ru_identitylink，act_ru_job，act_ru_task，act_ru_variable
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    //查询历史记录的服务器act_hi所有
    @Autowired
    private HistoryService historyService;

	/*//页面表单服务器(了解)
	@Autowired
	private FormServic formService;

	//对工作流的用户管理的表的操作act_id_group。act_id_info，act_id_membership，act_id_user
	@Autowired
	private IdentityService identityService;*/

    //管理器
    @Autowired
    private ManagementService managementService;

    //①部署流程第一种方式
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

    //①部署流程第二种方式
    //使用zip，流程图文件必须×××.zip结尾的文件
    @Test
    public void deployProcess2() throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/processes/JYDProcess.zip");
        ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
        System.out.println(resourceAsStream);
        Deployment deployment = repositoryService.createDeployment().name("jyd请假流程")
                .addZipInputStream(zipInputStream)//添加流程图的流
                .deploy();//确定部署
        System.out.println("部署成功,部署id：" + deployment.getId());
    }

    //②启动流程：注意数据库创建方式drop-create会导致每次查不到流程的Key
    @Test
    public void startProcess() {
        String processDefintionId = "";
        //runtimeService.startProcessInstanceById(processDefintionId);
        String processDfinationKey = "JYDProcess";
        runtimeService.startProcessInstanceByKey(processDfinationKey);
        System.out.println("流程启动成功");
    }

    //③查询任务
    @Test
    public void findTask() {
        String assignee = "jyddad";
        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().taskAssignee(assignee).list();
        if (!CollectionUtils.isEmpty(list)) {
            for (org.activiti.engine.task.Task task : list) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("流程实例ID:" + task.getProcessInstanceId());
                System.out.println("执行实例ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务办理人:" + task.getAssignee());
                System.out.println("#################");
            }
        }
    }

    //④办理任务
    @Test
    public void completeTask() {
        //查询任务的任务id
        String taskId = "6dfbd323-ccad-11ea-9d35-d0c637ab19a3";
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

    //查询流程部署信息 act_re_deployment
    @Test
    public void queryProcessDef() {
        //创建部署信息的查询
        String deploymentId = "e8e8d7a2-cd51-11ea-bda5-d0c637ab19a3";
        //Deployment deployment = repositoryService.createDeploymentQuery()
        //Long count = repositoryService.createDeploymentQuery()
        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                //条件
                //.deploymentId(deploymentId)//根据部署id查询
                //.deploymentName(deploymentName)//根据部署名称查询
                //.deploymentTenantId(tenantId)//根据tenantId查询
                //.deploymentNameLike(deploymentName)//根据部署名称模糊查询
                //.deploymentTenantIdLike(tenantId)//根据tenantId模糊查询
                //排序
                //.orderByDeploymentId().asc()//根据部署id升序
                //.orderByDeploymenTime().desc()//根据部署时间降序
                //.orderByDeploymentName().asc()//根据部署名称升序
                //结果集
                .list();//查询返回list集合
        //.listPage(firstResult，maxResult);//分页查询返回list集合
        //.singleResult();//返回单个对象
        //.count();
        /*System.out.println("部署ID:" + deployment.getId());
        System.out.println("部署名称:" + deployment.getName());
        System.out.println("部署时间:" + deployment.getDeploymentTime());*/
        /*System.out.println(count);*/
        for (Deployment deployment : deployments) {
            System.out.println("部署ID:" + deployment.getId());
            System.out.println("部署名称:" + deployment.getName());
            System.out.println("部署时间:" + deployment.getDeploymentTime());
            System.out.println("*****************************");
        }
    }

    //查询流程定义信息
    @Test
    public void queryProcDef() {
        //创建部署信息的查询
        String deploymentId = "e8e8d7a2-cd51-11ea-bda5-d0c637ab19a3";
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                //条件
                //.deploymentId(deploymentId)//根据部署id查询
                //.deploymentIds(deploymentIds)//根据部署id集合查询Set<String> deploymentIds
                //.processDefinitionId(processDefinitionId)//根据流程定义Id查询
                //.processDefinitionIds(processDefinitionId)//根据流程定义Ids查询
                //.processDefinitionKey(processDefinitionKey)//根据流程定义key查询
                //.processDefinitionKeyLike()//根据流程定义key模糊查询
                //.processDefinitionName()//根据流程定义的名称查询
                //.processDefinitionNameLike()//根据流程定义的名称模糊查询
                //.processDefinitionResourceName(resourceName)//根据流程图的bpmn文件名查询
                //.processDefinitionResourceNameLike()//根据流程图的bpmn文件名模糊查询
                //.processDefinitionVersion(processDefinitionVersion)//根据流程定义版本查询
                //.processDefinitionVersionGreaterThan(processDefinitionVersion)//version>num
                //.processDefinitionVersionGreaterThanOrEquals(processDefinitionVersion)//version>=num
                //.processDefinitionVersionLowerThan(processDefinitionVersion)//version<num
                //.processDefinitionVersionLowerThanOrEquals(processDefinitionVersion)//version<=num
                //排序
                //.orderByDeploymentId()//根据部署id升序
                //.orderByProcessDefinitionId()//根据部署时间降序
                //.orderByProcessDefinitionName()//根据部署名称升序
                //.orderByProcessDefinitionKey()
                //.orderByProcessDefinitionVersion()
                //结果集
                .list();//查询返回list集合
        //.listPage(firstResult，maxResult);//分页查询返回list集合
        //.singleResult();//返回单个对象
        //.count();
        /*System.out.println("部署ID:" + deployment.getId());
        System.out.println("部署名称:" + deployment.getName());
        System.out.println("部署时间:" + deployment.getDeploymentTime());*/
        /*System.out.println(count);*/
        for (org.activiti.engine.repository.ProcessDefinition pd : processDefinitions) {
            System.out.println("流程定义ID:" + pd.getId());
            System.out.println("流程部署ID:" + pd.getDeploymentId());
            System.out.println("流程定义key:" + pd.getKey());
            System.out.println("流程定义名称:" + pd.getName());
            System.out.println("流程定义bpmn文件名:" + pd.getResourceName());//bpmn的name
            System.out.println("流程图片名:" + pd.getDiagramResourceName());//png的name
            System.out.println("流程版本号:" + pd.getVersion());
        }
    }

    //删除流程定义
    @Test // 删除流程
    public void deleteProcessDef() {
        String deploymentId = "";
        //根据流程部署id删除，删除流程定义，如果当前id的流程正在执行，那么会报错
        repositoryService.deleteDeployment(deploymentId);
        //根据流程部署id删除流程定义，如果当前id的流程正在执行，会把正在执行的流程数据删除act_ru_*和act_hi_*里面的数据
        //repositoryService.deleteDeployment(deploymentId,true);
        System.out.println("删除成功");
    }

    //方法可以实现，需要注意路径需要存在
    //查询流程图，根据流程部署id
    @Test // 查看流程
    public void viewProcessImg1() {

        String deploymentId = "61589d24-ccac-11ea-8810-d0c637ab19a3";
        List<String> names = repositoryService.getDeploymentResourceNames(deploymentId);
        String imageName = null;
        for (String name : names) {
            if (name.indexOf(".png") >= 0) {
                imageName = name;
            }
        }
        if (imageName != null) {
            //此处获取InputStream时采用该方式，因为repositoryService没有对应的方法getProcessDiagram(processDefId);此方法根据流程定义id查询流程图
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, imageName);
            //注意此处获取的路径是相对路径也就是图片所在resources的相对路径为processes/JYDProcess.png，此处写死路径文件格式打不开
            File file = new File("d:/" + imageName);
            try {
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                int len = 0;
                byte[] b = new byte[1024];
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                    outputStream.flush();
                }
                outputStream.close();
                inputStream.close();
                System.out.println("查询成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    //getProcessModel这个方法获取流不可以，文件打不开
    //查询流程图，根据流程部署id获取流程定义id
    @Test // 查看流程
    public void viewProcessImg2() {

        String deploymentId = "61589d24-ccac-11ea-8810-d0c637ab19a3";
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, processDefinition.getDiagramResourceName());
        //表内存储的图表名也是相对路径
        String imageName= processDefinition.getDiagramResourceName();
        File file = new File("d:/" + imageName);
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
            System.out.println("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //getProcessModel这个方法获取流不可以，必须使用getResourceAsStream获取流，否则文件打不开，文件路径无影响
    //查询流程图，根据流程定义id
    @Test // 查看流程
    public void viewProcessImgByProcessDefinedId() {
        String processDefinitionId = "JYDProcess:2:7636907c-ccac-11ea-ae99-d0c637ab19a3";
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),processDefinition.getDiagramResourceName());

        File file = new File("d:/JYDProcess.png");
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
            System.out.println("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // 查看最新流程定义
    @Test
    public void queryNewProcessDef() {
        Map<String,ProcessDefinition> map = new HashMap<>();

        //查询所有流程定义根据版本号升序,必须加asc()或者desc()否则报错
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionVersion().asc().list();
        if (!CollectionUtils.isEmpty(list)) {
            for (ProcessDefinition pd : list) {
                map.put(pd.getKey(),pd);
            }
        }
        //循环map集合
        Collection<ProcessDefinition> values = map.values();
        for (ProcessDefinition pd:  values) {
            System.out.println("流程定义ID:" + pd.getId());
            System.out.println("流程部署ID:" + pd.getDeploymentId());
            System.out.println("流程定义key:" + pd.getKey());
            System.out.println("流程定义名称:" + pd.getName());
            System.out.println("流程定义bpmn文件名:" + pd.getResourceName());//bpmn的name
            System.out.println("流程图片名:" + pd.getDiagramResourceName());//png的name
            System.out.println("流程版本号:" + pd.getVersion());
            System.out.println("*************************");
        }

    }

    //已知key
    //删除流程定义(删除key相同的所有不同版本的流程定义)
    @Test
    public void deleteAllSameVersion() {
        String processDefinitionKey="JYDProcess";
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list();
        if (!CollectionUtils.isEmpty(list)) {

            for (ProcessDefinition pd:list) {
                String deploymentId = pd.getDeploymentId();
                repositoryService.deleteDeployment(deploymentId,true);
            }
        }
    }

    @Test // 查看流程
    public void deploy() {
        securityUtil.logInAs("salaboy");
        String bpmnName = "MyProcess";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("请假流程");
        Deployment deployment = null;
        try {
            deployment = deploymentBuilder.addClasspathResource("processes/" + bpmnName + ".bpmn")
                    .addClasspathResource("processes/" + bpmnName + ".png").deploy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test // 远程外部BPMN
    public void deploy2() {
        securityUtil.logInAs("salaboy");
        try {
            Deployment deployment = null;
            InputStream in = new FileInputStream(new File("C:\\Users\\飞牛\\git\\SpringBoot2_Activiti7\\src\\main\\resources\\processes\\leaveProcess.zip"));
            ZipInputStream zipInputStream = new ZipInputStream(in);
            deployment = repositoryService.createDeployment().name("请假流程2")
                    // 指定zip格式的文件完成部署
                    .addZipInputStream(zipInputStream).deploy();// 完成部署
            zipInputStream.close();
        } catch (Exception e) {
            // TODO 上线时删除
            e.printStackTrace();
        }
    }

    @Test // 查看流程  act_hi_procinst
    public void contextLoads() {
        securityUtil.logInAs("salaboy");
        Page processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
        System.err.println("已部署的流程个数：" + processDefinitionPage.getTotalItems());
        for (Object obj : processDefinitionPage.getContent()) {
            System.err.println("流程定义：" + obj);
        }
    }

    @Test // 启动流程   在act_ru_task查看任务结点
    public void startInstance() {
        securityUtil.logInAs("salaboy");
        ProcessInstance processInstance = processRuntime
                .start(ProcessPayloadBuilder.start().withProcessDefinitionKey("myProcess_1").build());
        System.err.println("流程实例ID：" + processInstance.getId());
    }

    @Test // 执行流程  查看表act_ru_execution
    public void testTask() {
        securityUtil.logInAs("salaboy");
        Page<Task> page = taskRuntime.tasks(Pageable.of(0, 10));
        if (page.getTotalItems() > 0) {
            for (Task task : page.getContent()) {
                System.err.println("当前任务有1：" + task);
                // 拾取
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                // 执行
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            }
        } else {
            System.err.println("没的任务1");
        }

        page = taskRuntime.tasks(Pageable.of(0, 10));
        if (page.getTotalItems() > 0) {
            for (Task task : page.getContent()) {
                System.err.println("当前任务有2：" + task);
            }
        } else {
            System.err.println("没的任务2");
        }
    }

}
