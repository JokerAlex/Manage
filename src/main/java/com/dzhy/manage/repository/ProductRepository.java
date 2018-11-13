package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName ProductRepository
 * @Description product
 * @Author alex
 * @Date 2018/10/30
 **/
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByProductId(Integer productId);

    boolean existsByProductName(String productName);

    Product findByProductName(String productName);

    void deleteAllByProductIdIn(List<Integer> productIds);

    Page<Product> findAllByProductNameContaining(String productName, Pageable pageable);

    List<Product> findAllByProductNameContaining(String productName, Sort sort);

    List<Product> findByProductIdIn(List<Integer> productIds);
}
