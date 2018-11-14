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
 * @ClassName Output
 * @Description 产值
 * @Author alex
 * @Date 2018/10/30
 **/
@Entity
@DynamicInsert
@DynamicUpdate
@Data
@Accessors(chain = true)
public class Output {

    @Id
    @GeneratedValue
    private Integer outputId;

    private Integer outputYear;

    private Integer outputMonth;

    private Integer outputProductId;

    private String outputProductName;

    private Integer outputXiadan;

    private Integer outputMugong;

    private Integer outputYoufang;

    private Integer outputBaozhuang;

    private Float outputBaozhuangTotalPrice;

    private Integer outputTeding;

    private Float outputTedingTotalPrice;

    private Integer outputBeijing;

    private Integer outputBeijingteding;

    private Date outputCreateTime;

    private Date outputUpdateTime;

    public Output() {
    }
}
