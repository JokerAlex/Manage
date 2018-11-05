package com.dzhy.manage.service.impl;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Product;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName ProductServiceImplTest
 * @Description 产品管理测试
 * @Author alex
 * @Date 2018/10/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void checkProductName() {
        ResponseDTO r = productService.checkProductName("haha");
        System.out.println(r.toString());
        Assert.assertEquals(0, r.getStatus());
    }

    @Test
    public void addProduct() {
        Product product = new Product();
        product.setProductName("chuang6");
        product.setProductPrice(100F);
        product.setProductComment("test----");
        ResponseDTO r = productService.addProduct(product);
        System.out.println(r.toString());
        Assert.assertEquals(0, r.getStatus());
    }

    @Test
    public void updateProduct() {
        Product product = new Product();
        product.setProductId(13);
        product.setProductName("chuang3");
        product.setProductPrice(1000000F);
        ResponseDTO r = productService.updateProduct(product);
        System.out.println(r.toString());
        Assert.assertEquals(1, r.getStatus());
    }

    @Test
    public void deleteProduct() {
        ResponseDTO r = productService.deleteProduct(7);
        System.out.println(r.toString());
        Assert.assertEquals(1, r.getStatus());
    }

    @Test
    public void deleteProductBatch() {
        List<Integer> list = Arrays.asList(8,9);
        ResponseDTO r = productService.deleteProductBatch(list);
        System.out.println(r.toString());
        Assert.assertEquals(0, r.getStatus());
    }

    @Test
    public void listAllProduct() {
        ResponseDTO r = productService.listAllProduct("5");
        System.out.println(r.toString());
    }

    @Test
    public void listProduct() {
        ResponseDTO r = productService.listProduct(1,5, null);
        System.out.println(r.toString());
        Assert.assertEquals(0, r.getStatus());
    }

    @Test
    public void getDetails() {
        ResponseDTO r = productService.getDetails(12);
        System.out.println(r.toString());
        Assert.assertEquals(0, r.getStatus());
    }
}