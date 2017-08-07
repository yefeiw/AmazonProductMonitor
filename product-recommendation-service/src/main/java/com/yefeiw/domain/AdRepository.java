package com.yefeiw.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

/**
 * Created by vagrant on 8/5/17.
 */
public interface AdRepository extends JpaRepository<Ad,Long> {
    Ad findAdByAsin(String asin);
    @Transactional
    @Modifying
    Ad save(Ad ad);
}
