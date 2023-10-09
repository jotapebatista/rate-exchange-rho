package com.rho.rateexhchange.repository;

import com.rho.rateexhchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    /**
     * Find an exchange rate by its currency code.
     *
     * @param currency The currency code.
     * @return The matching exchange rate, if found.
     */
    @Query("SELECT e FROM ExchangeRate e WHERE e.currency = :currency")
    ExchangeRate findByCurrency(@Param("currency") String currency);

}