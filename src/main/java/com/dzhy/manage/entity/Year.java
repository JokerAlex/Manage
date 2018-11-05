package com.dzhy.manage.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName Year
 * @Description year
 * @Author alex
 * @Date 2018/10/30
 **/
@Entity
@Data
@DynamicInsert
@DynamicUpdate
public class Year {

    @Id
    private Integer yearId;

    private Date yearCreateTime;

    private Date yearUpdateTime;

    public Year() {
    }

}
