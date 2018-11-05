package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Output;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName OutputRepository
 * @Description output
 * @Author alex
 * @Date 2018/10/30
 **/
public interface OutputRepository extends JpaRepository<Output, Integer> {

    boolean existsByOutputYearAndOutputMonthAndOutputProductId(Integer year, Integer month, Integer productId);

    boolean existsByOutputYearAndOutputMonth(Integer year, Integer month);

    Output findByOutputId(Integer outputId);

    Output findByOutputYearAndOutputMonthAndOutputProductId(Integer year, Integer month, Integer productId);

    Page<Output> findAllByOutputYearAndAndOutputMonth(Integer year, Integer month, Pageable pageable);

    Page<Output> findAllByOutputYearAndAndOutputMonthAndOutputProductNameContaining(Integer year, Integer month, String outputProductName, Pageable pageable);

    List<Output> findAllByOutputYearAndAndOutputMonth(Integer year, Integer month);

    List<Output> findAllByOutputProductId(Integer productId);
}
