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
 * @ClassName Produce
 * @Description 进度
 * @Author alex
 * @Date 2018/10/30
 **/
@Entity
@DynamicInsert
@DynamicUpdate
@Accessors(chain = true)
@Data
public class Produce {

    @Id
    @GeneratedValue
    private Integer produceId;

    private Integer produceYear;

    private Integer produceMonth;

    private Integer produceDay;

    private Integer produceProductId;

    private String produceProductName;

    private Integer produceXiadan;

    private String produceXiadanComment;

    private Integer produceMugong;

    private String produceMugongComment;

    private Integer produceYoufang;

    private String produceYoufangComment;

    private Integer produceBaozhuang;

    private String produceBaozhuangComment;

    private Integer produceTeding;

    private String produceTedingComment;

    private Integer produceBeijing;

    private String produceBeijingComment;

    private Integer produceBeijingteding;

    private String produceBeijingtedingComment;

    private Integer produceBendihetong;

    private String produceBendihetongComment;

    private Integer produceWaidihetong;

    private String produceWaidihetongComment;

    private Integer produceDeng;

    private String produceDengComment;

    private Date produceCreateTime;

    private Date produceUpdateTime;

    public Produce() {
    }

    public Produce(Integer produceYear, Integer produceMonth, Integer produceDay,
                   Integer produceProductId, String produceProductName,
                   Integer produceXiadan, String produceXiadanComment, Integer produceMugong, String produceMugongComment,
                   Integer produceYoufang, String produceYoufangComment, Integer produceBaozhuang, String produceBaozhuangComment,
                   Integer produceTeding, String produceTedingComment, Integer produceBeijing, String produceBeijingComment,
                   Integer produceBeijingteding, String produceBeijingtedingComment, Integer produceBendihetong, String produceBendihetongComment,
                   Integer produceWaidihetong, String produceWaidihetongComment, Integer produceDeng, String produceDengComment) {
        this.produceYear = produceYear;
        this.produceMonth = produceMonth;
        this.produceDay = produceDay;
        this.produceProductId = produceProductId;
        this.produceProductName = produceProductName;
        this.produceXiadan = produceXiadan;
        this.produceXiadanComment = produceXiadanComment;
        this.produceMugong = produceMugong;
        this.produceMugongComment = produceMugongComment;
        this.produceYoufang = produceYoufang;
        this.produceYoufangComment = produceYoufangComment;
        this.produceBaozhuang = produceBaozhuang;
        this.produceBaozhuangComment = produceBaozhuangComment;
        this.produceTeding = produceTeding;
        this.produceTedingComment = produceTedingComment;
        this.produceBeijing = produceBeijing;
        this.produceBeijingComment = produceBeijingComment;
        this.produceBeijingteding = produceBeijingteding;
        this.produceBeijingtedingComment = produceBeijingtedingComment;
        this.produceBendihetong = produceBendihetong;
        this.produceBendihetongComment = produceBendihetongComment;
        this.produceWaidihetong = produceWaidihetong;
        this.produceWaidihetongComment = produceWaidihetongComment;
        this.produceDeng = produceDeng;
        this.produceDengComment = produceDengComment;
    }
}
