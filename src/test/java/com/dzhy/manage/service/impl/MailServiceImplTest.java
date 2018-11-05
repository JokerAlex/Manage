package com.dzhy.manage.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ClassName MailServiceImplTest
 * @Description mail test
 * @Author alex
 * @Date 2018/11/4
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceImplTest {

    @Autowired
    private MailServiceImpl mailService;

    @Test
    public void sendSimpleMail() {
        String to = "alex.zhao1023@gmail.com";
        String subject = "mail test";
        String content = "这是第一封邮件测试aaaaaaaaaaaaaaaaaa";
        mailService.sendSimpleMail(to, subject, content);
    }

    @Test
    public void sendAttachmentsMailTest() {
        String to = "1456395363@qq.com";
        String subject = "fujian";
        String content = "这是第一封邮件";
        String filePath = "/Users/alex/Desktop/2018114进度报表.xlsx";
        mailService.sendAttachmentsMail(to, subject, content, filePath);
    }
}