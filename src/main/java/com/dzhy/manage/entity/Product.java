package com.dzhy.manage.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName Product
 * @Description 产品信息
 * @Author alex
 * @Date 2018/10/30
 **/
@Entity
@Data
@DynamicUpdate
@DynamicInsert
public class Product {

    @Id
    @GeneratedValue
    private Integer productId;

    private String productName;

    private Float productPrice;

    private String productComment;

    private Date productCreateTime;

    private Date productUpdateTime;

    public Product() {
    }

    public Product(Integer productId, String productName, float productPrice, String productComment, Date productCreateTime, Date productUpdateTime) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productComment = productComment;
        this.productCreateTime = productCreateTime;
        this.productUpdateTime = productUpdateTime;
    }
}
