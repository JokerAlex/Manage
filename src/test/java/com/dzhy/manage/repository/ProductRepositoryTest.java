package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ClassName ProductRepositoryTest
 * @Description test
 * @Author alex
 * @Date 2018/10/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    public void saveTest() {
        Product product = new Product();
        product.setProductName("chuang");
        product.setProductPrice(100F);
        product.setProductComment("test");
        repository.save(product);
    }
}