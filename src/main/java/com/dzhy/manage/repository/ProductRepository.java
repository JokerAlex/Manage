package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @ClassName ProductRepository
 * @Description product
 * @Author alex
 * @Date 2018/10/30
 **/
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    Product findByProductId(Integer productId);

    boolean existsByProductName(String productName);

    Product findByProductName(String productName);

    void deleteAllByProductIdIn(List<Integer> productIds);

    List<Product> findByProductIdIn(List<Integer> productIds);
}
