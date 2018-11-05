package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Year;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ClassName YearRepositoryTest
 * @Description year
 * @Author alex
 * @Date 2018/10/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class YearRepositoryTest {

    @Autowired
    private YearRepository repository;

    @Test
    public void saveTest() {
        Year year = new Year();
        year.setYearId(2018);
        repository.save(year);
    }
}