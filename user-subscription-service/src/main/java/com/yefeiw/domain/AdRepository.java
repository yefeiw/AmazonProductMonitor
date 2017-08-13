package com.yefeiw.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by vagrant on 8/5/17.
 */
public interface AdRepository extends JpaRepository<Ad,Long> {
    Ad findAdByAsin(@Param("asin") String asin);
    List<Ad> findAllByCategoryOrderByDiscount(@Param("category") String category);
    @Transactional
    @Modifying
    Ad save(Ad ad);
}

