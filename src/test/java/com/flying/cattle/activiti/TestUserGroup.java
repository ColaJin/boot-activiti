package com.flying.cattle.activiti;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 用户组的处理
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUserGroup {
    /*@Autowired
    private IdentityService identityService;

    *//**
     * 创建用户和用户组
     *//*

    @Test
    public void createUserAndGroup() {
        //保存在act_id_group
        GroupEntity group = new GroupEntity("1");
        group.setName("部门经理");
        identityService.saveGroup(group);
        GroupEntity group2 = new GroupEntity("2");
        group.setName("总经理");
        identityService.saveGroup(group2);

        //保存到act_id_user
        UserEntity user1 = new UserEntity("1");
        user1.setFirstName("小明");
        user1.setEmail("1111@aa.com");
        identityService.saveUser(user1);
        UserEntity user2 = new UserEntity("2");
        user2.setFirstName("小张");
        user2.setEmail("1111@aa.com");
        identityService.saveUser(user2);
        UserEntity user3 = new UserEntity("3");
        user1.setFirstName("小王");
        user1.setEmail("1111@aa.com");
        identityService.saveUser(user3);

        //建立用户和用户组的关系act_id_membership
        identityService.createMembership("1","1");
        identityService.createMembership("2","1");
        identityService.createMembership("3","2");

        System.out.println("保存成功");
    }*/
}
