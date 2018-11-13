package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName CategoryRepository
 * @Description 产品类别
 * @Author alex
 * @Date 2018/11/13
 **/
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByCategoryName(String categoryName);

    Category findByCategoryId(Integer categoryId);

    void deleteByCategoryIdIn(List<Integer> categoryIds);

    Page<Category> findAllByCategoryNameContaining(String categoryName, Pageable pageable);
}
