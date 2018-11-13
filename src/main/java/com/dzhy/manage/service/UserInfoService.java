package com.dzhy.manage.service;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * @ClassName UserInfoService
 * @Description 用户信息管理
 * @Author alex
 * @Date 2018/11/2
 **/
public interface UserInfoService {

    ResponseDTO checkUsername(String username) throws ParameterException;

    ResponseDTO addUserInfo(UserInfo userInfo) throws ParameterException, GeneralException;

    ResponseDTO updateUserInfo(UserInfo userInfo, int who) throws ParameterException, GeneralException;

    ResponseDTO resetPassword(Integer userInfoId, String pass) throws ParameterException, GeneralException;

    ResponseDTO changePassword(String oldPass, String newPass) throws ParameterException, GeneralException, AccessDeniedException;

    ResponseDTO deleteUserInfo(List<Integer> userIds) throws ParameterException, GeneralException;

    ResponseDTO listUserInfo(Integer pageNum, Integer pageSize, String userTrueName);
}
