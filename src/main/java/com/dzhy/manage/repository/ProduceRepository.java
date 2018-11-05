package com.dzhy.manage.repository;

import com.dzhy.manage.entity.Produce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName ProduceRepository
 * @Description produce
 * @Author alex
 * @Date 2018/10/30
 **/
public interface ProduceRepository extends JpaRepository<Produce, Integer> {

    boolean existsByProduceProductName(String productName);

    boolean existsByProduceYearAndProduceMonthAndProduceDay(Integer produceYear, Integer produceMonth, Integer produceDay);

    boolean existsByProduceYearAndProduceMonthAndProduceDayAndProduceProductId(Integer produceYear, Integer produceMonth, Integer produceDay, Integer produceProductId);

    Produce findByProduceId(Integer produceId);

    void deleteAllByProduceIdIn(List<Integer> produceIds);

    void deleteAllByProduceYearAndAndProduceMonthAndProduceDay(Integer produceYear, Integer produceMonth, Integer produceDay);

    Page<Produce> findAllByProduceYearAndProduceMonthAndProduceDay(Integer produceYear, Integer produceMonth, Integer produceDay, Pageable pageable);

    Page<Produce> findAllByProduceYearAndProduceMonthAndProduceDayAndProduceProductNameContaining(Integer produceYear, Integer produceMonth, Integer produceDay, String produceProductName, Pageable pageable);

    List<Produce> findAllByProduceYearAndProduceMonthAndProduceDay(Integer produceYear, Integer produceMonth, Integer produceDay);
}
