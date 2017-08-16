package com.yefeiw.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by vagrant on 8/5/17.
 */
public interface AdRepository extends JpaRepository<Ad,String> {
    Ad findAdByAsin(String asin);
    List<Ad> findFirst100ByCategoryOrderByDiscountDesc(String category);
    @Transactional
    @Modifying
    Ad save(Ad ad);
    @Transactional
    @Modifying
    void removeByAsin(String asin);
}
