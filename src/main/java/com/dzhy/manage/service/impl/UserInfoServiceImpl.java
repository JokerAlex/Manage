package com.dzhy.manage.service.impl;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.repository.UserInfoRepository;
import com.dzhy.manage.security.entity.JwtUserDetails;
import com.dzhy.manage.service.UserInfoService;
import com.dzhy.manage.util.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName UserInfoServiceImpl
 * @Description 用户信息管理
 * @Author alex
 * @Date 2018/11/2
 **/
@Service("iUserInfoService")
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseDTO checkUsername(String username) throws ParameterException {
        if (StringUtils.isBlank(username)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        if (userInfoRepository.existsByUserInfoName(username)) {
            return ResponseDTO.isError(ResultEnum.IS_EXIST.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO addUserInfo(UserInfo userInfo) throws ParameterException, GeneralException {
        if (userInfo == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        ResponseDTO r = this.checkUsername(userInfo.getUserInfoName());
        if (!r.isOk()) {
            return r;
        }

        UserInfo user = new UserInfo();
        user.setUserInfoName(userInfo.getUserInfoName());
        user.setUserInfoPass(passwordEncoder.encode(userInfo.getUserInfoPass()));
        user.setUserInfoTrueName(userInfo.getUserInfoTrueName());
        user.setUserInfoRoles(userInfo.getUserInfoRoles());
        try {
            userInfoRepository.save(user);
            log.info("[addUserInfo] user = {}", user.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO updateUserInfo(UserInfo userInfo) throws ParameterException, GeneralException {
        if (userInfo == null || userInfo.getUserInfoId() == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        UserInfo userSource = userInfoRepository.findByUserInfoId(userInfo.getUserInfoId());
        if (userSource == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getCode() + "-ID:" + userInfo.getUserInfoId());
        }
        UserInfo user = new UserInfo();
        user.setUserInfoTrueName(userInfo.getUserInfoTrueName());
        user.setUserInfoRoles(userInfo.getUserInfoRoles());
        user.setUserInfoIsLocked(userInfo.getUserInfoIsLocked());
        UpdateUtils.copyNullProperties(userSource, user);
        try {
            userInfoRepository.save(user);
            log.info("[updateUserInfo] user = {}", user.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO changePassword(String oldPass, String newPass) throws ParameterException, GeneralException {
        if (StringUtils.isBlank(oldPass) || StringUtils.isBlank(newPass)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (details == null) {
            throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-用户信息");
        }
        UserInfo userInfoSource = userInfoRepository.findByUserInfoName(details.getUsername());

        if (!passwordEncoder.matches(oldPass, userInfoSource.getUserInfoPass())) {
            return ResponseDTO.isError("原密码错误");
        }
        if (oldPass.equals(newPass)) {
            return ResponseDTO.isError("新密码与原密码相同");
        }
        userInfoSource.setUserInfoPass(passwordEncoder.encode(newPass));
        try {
            userInfoRepository.save(userInfoSource);
            log.info("[changePassword] userInfoSource = {}", userInfoSource.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException("修改密码失败");
        }
        SecurityContextHolder.clearContext();
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteUserInfo(List<Integer> userIds) throws ParameterException, GeneralException {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        try {
            userInfoRepository.deleteAllByUserInfoIdIn(userIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO listUserInfo(Integer pageNum, Integer pageSize, String userTrueName) {
        if (pageNum == null || pageSize == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "userInfoName");
        Page<UserInfo> userInfoPage;
        if (StringUtils.isBlank(userTrueName)) {
            userInfoPage = userInfoRepository.findAll(pageable);
        } else {
            userInfoPage = userInfoRepository.findAllByUserInfoTrueNameContaining(userTrueName, pageable);
        }
        return ResponseDTO.isSuccess(userInfoPage);
    }
}
