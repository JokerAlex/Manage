package com.dzhy.manage.repository;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.util.UpdateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ClassName UserInfoRepositoryTest
 * @Description user info
 * @Author alex
 * @Date 2018/10/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoRepositoryTest {

    @Autowired
    private UserInfoRepository repository;

    @Test
    public void saveTest() {
        UserInfo userInfo = new UserInfo();
        //userInfo.setUserInfoId(3);
        userInfo.setUserInfoName("test");
        userInfo.setUserInfoPass("123456");
        userInfo.setUserInfoTrueName("zzz");
        userInfo.setUserInfoRoles("USER");
        UserInfo u = repository.saveAndFlush(userInfo);
        System.out.println(u.toString());
        System.out.println(userInfo.toString());
    }

    @Test
    public void saveTest2() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoName("hahah");
        userInfo.setUserInfoPass("123456");
        userInfo.setUserInfoTrueName("zzz");
        userInfo.setUserInfoRoles("USER");
        UserInfo u = repository.save(userInfo);
        System.out.println(u.toString());
        System.out.println(userInfo.toString());
    }

    @Test
    public void updateTest() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoId(3);
        userInfo.setUserInfoName("hahah");
        UserInfo source = repository.findByUserInfoId(userInfo.getUserInfoId());
        UpdateUtils.copyNullProperties(source, userInfo);
        repository.save(userInfo);
        System.out.println(userInfo.toString());
    }
}