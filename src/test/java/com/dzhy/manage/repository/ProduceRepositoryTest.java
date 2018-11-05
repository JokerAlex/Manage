package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Produce;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ClassName ProduceRepositoryTest
 * @Description produce
 * @Author alex
 * @Date 2018/10/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProduceRepositoryTest {

    @Autowired
    private ProduceRepository repository;

    @Test
    public void saveTest() {
        Produce produce = new Produce();
        produce.setProduceYear(2018);
        produce.setProduceMonth(10);
        produce.setProduceDay(30);
        produce.setProduceProductId(1);
        produce.setProduceProductName("chuang");
        Produce p = repository.save(produce);
        System.out.println(p.toString());
    }
}