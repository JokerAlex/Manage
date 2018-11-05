package com.dzhy.manage.service.impl;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.dto.ResponseDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName UserInfoServiceImplTest
 * @Description test
 * @Author alex
 * @Date 2018/11/2
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoServiceImplTest {

    @Autowired
    private UserInfoServiceImpl userInfoService;


    @Test
    public void addUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoName("test");
        userInfo.setUserInfoTrueName("zzz");
        userInfo.setUserInfoPass("123456");
        userInfo.setUserInfoRoles("ADMIN");
        ResponseDTO r = userInfoService.addUserInfo(userInfo);
        System.out.println(r.toString());
    }

    @Test
    public void updateUserInfo() {
    }

    @Test
    public void deleteUserInfo() {
    }

    @Test
    public void listUserInfo() {
    }
}