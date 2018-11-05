package com.dzhy.manage.service;

import com.dzhy.manage.dto.ResponseDTO;

/**
 * @ClassName MailService
 * @Description 邮件服务
 * @Author alex
 * @Date 2018/11/4
 **/
public interface MailService {

    ResponseDTO sendSimpleMail(String to, String subject, String content);

    ResponseDTO sendAttachmentsMail(String to, String subject, String content, String filePath);
}
