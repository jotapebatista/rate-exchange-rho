package com.rho.rateexhchange.repository;

import com.rho.rateexhchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT e FROM ExchangeRate e WHERE e.currency = :currency")
    ExchangeRate findByCurrency(@Param("currency") String currency);

    @Query("SELECT e FROM ExchangeRate e WHERE e.currency LIKE CONCAT(:from, '%')")
    ExchangeRate findByCurrencyStartingWith(@Param("from") String from);

}