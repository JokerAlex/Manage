package com.dzhy.manage.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName Category
 * @Description 产品类别
 * @Author alex
 * @Date 2018/11/13
 **/
@Entity
@Data
@DynamicUpdate
@DynamicInsert
@Accessors(chain = true)
public class Category {

    @Id
    @GeneratedValue
    private Integer categoryId;

    private String categoryName;

    private Date categoryCreateTime;

    private Date categoryUpdateTime;
}
