package com.dzhy.manage.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName UserInfo
 * @Description 用户信息
 * @Author alex
 * @Date 2018/10/30
 **/
@Entity
@Data
@DynamicInsert
@DynamicUpdate
public class UserInfo {

    @Id
    @GeneratedValue
    private Integer userInfoId;

    private String userInfoName;

    private String userInfoPass;

    private String userInfoTrueName;

    private String userInfoRoles;

    private Integer userInfoIsLocked;

    private Date userInfoCreateTime;

    private Date userInfoUpdateTime;

    public UserInfo() {
    }
}
