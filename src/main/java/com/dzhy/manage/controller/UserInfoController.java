package com.dzhy.manage.controller;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.service.UserInfoService;
import com.dzhy.manage.dto.ResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName UserInfoController
 * @Description 用户管理 controller
 * @Author alex
 * @Date 2018/11/2
 **/
@RestController
@RequestMapping("/user")
@Api(value = "用户信息", description = "用户信息管理")
public class UserInfoController {
    private final UserInfoService iUserInfoService;


    public UserInfoController(UserInfoService iUserInfoService) {
        this.iUserInfoService = iUserInfoService;
    }

    @ApiOperation(value = "检查用户名", notes = "检查用户名是否可用")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataTypeClass =String.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'UESER')")
    @GetMapping("/check")
    public ResponseDTO checkUsername(@RequestParam(value = "username") String username) {
        return iUserInfoService.checkUsername(username);
    }

    @ApiOperation(value = "添加", notes = "添加新用户")
    @ApiImplicitParam(name = "userInfo", value = "用户实体类", required = true, dataTypeClass = UserInfo.class)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping()
    public ResponseDTO addUserInfo(@RequestBody UserInfo userInfo) {
        return iUserInfoService.addUserInfo(userInfo);
    }

    @ApiOperation(value = "管理员更新", notes = "更新用户用户信息")
    @ApiImplicitParam(name = "userInfo", value = "用户实体类", required = true, dataTypeClass = UserInfo.class)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/admin")
    public ResponseDTO updateUserInfoAdmin(@RequestBody UserInfo userInfo) {
        return iUserInfoService.updateUserInfo(userInfo, Constants.ADMIN_UPDATE_USER_INFO);
    }

    @ApiOperation(value = "其他用户更新", notes = "更新用户用户信息")
    @ApiImplicitParam(name = "userInfo", value = "用户实体类", required = true, dataTypeClass = UserInfo.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'UESER')")
    @PutMapping("/other")
    public ResponseDTO updateUserInfoOther(@RequestBody UserInfo userInfo) {
        return iUserInfoService.updateUserInfo(userInfo, Constants.OTHER_UPDAT_USER_INFOE);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPass", value = "原密码", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "newPass", value = "新密码", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'UESER')")
    @PutMapping("/pass")
    public ResponseDTO changePassword(@RequestParam(value = "oldPass") String oldPass,
                                      @RequestParam(value = "newPass") String newPass) {
        return iUserInfoService.changePassword(oldPass, newPass);
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userInfoId", value = "用户ID", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pass", value = "新密码", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/reset")
    public ResponseDTO resetPassword(@RequestParam(value = "userInfoId") Integer userInfoId,
                                     @RequestParam(value = "pass") String pass) {
        return iUserInfoService.resetPassword(userInfoId, pass);
    }

    @ApiOperation(value = "删除", notes = "删除用户")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping()
    public ResponseDTO deleteUserInfo(@RequestParam(value = "userIds[]") List<Integer> userIds) {
        return iUserInfoService.deleteUserInfo(userIds);
    }

    @ApiOperation(value = "列表", notes = "获取用户信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "userTrueName", value = "用户真实名称，模糊查询使用", dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping()
    public ResponseDTO listUserInfo(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                    @RequestParam(value = "userTrueName") String userTrueName) {
        return iUserInfoService.listUserInfo(pageNum, pageSize, userTrueName);
    }
}
