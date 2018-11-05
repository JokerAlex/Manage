package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Output;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ClassName OutputRepositoryTest
 * @Description output
 * @Author alex
 * @Date 2018/10/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class OutputRepositoryTest {
    @Autowired
    private OutputRepository repository;

    @Test
    public void save() {
        Output output = new Output();
        output.setOutputYear(2018);
        output.setOutputMonth(10);
        output.setOutputProductId(1);
        output.setOutputProductName("chuang");
        Output output1 = repository.save(output);
        System.out.println(output1.toString());
    }
}