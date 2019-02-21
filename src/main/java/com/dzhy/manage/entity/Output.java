package com.dzhy.manage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@AllArgsConstructor
public class Output {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer outputId;

    private Integer outputYear;

    private Integer outputMonth;

    private Integer outputProductId;

    private String outputProductName;

    private Integer outputXiadan;

    private Integer outputMugong;

    private Float outputMugongTotalPrice;

    private Integer outputYoufang;

    private Float outputYoufangTotalPrice;

    private Integer outputBaozhuang;

    private Float outputBaozhuangTotalPrice;

    private Integer outputTeding;

    private Float outputTedingTotalPrice;

    private Integer outputBeijingInput;

    private Float outputBeijingInputTotalPrice;

    private Integer outputBeijingtedingInput;

    private Float outputBeijingtedingInputTotalPrice;

    private Integer outputFactoryOutput;

    private Float outputFactoryOutputTotalPrice;

    private Integer outputTedingFactoryOutput;

    private Float outputTedingFactoryOutputTotalPrice;

    private Integer outputBeijingStock;

    private Float outputBeijingStockTotalPrice;

    private Integer outputBeijingtedingStock;

    private Float outputBeijingtedingStockTotalPrice;

    private Date outputCreateTime;

    private Date outputUpdateTime;

    public Output() {
    }
}
