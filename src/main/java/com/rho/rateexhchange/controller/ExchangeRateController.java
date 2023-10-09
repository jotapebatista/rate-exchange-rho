package com.rho.rateexhchange.controller;

import com.rho.rateexhchange.model.ExchangeRate;
import com.rho.rateexhchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateController(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    /**
     * Get the exchange rate from one currency to another.
     *
     * @param from The source currency.
     * @param to   The target currency.
     * @return ResponseEntity containing the exchange rate as "exchangeRate" in a map.
     */
    @GetMapping("/exchange-rate")
    public ResponseEntity<Map<String, BigDecimal>> getExchangeRate(@RequestParam("from") String from, @RequestParam("to") String to) {
        Map<String, BigDecimal> response = new HashMap<>();

        if (from.toUpperCase().equals(to.toUpperCase())) {
            response.put("exchangeRate", BigDecimal.ONE);
        } else {
            ExchangeRate fromRate = exchangeRateRepository.findByCurrency("USD" + from.toUpperCase());
            ExchangeRate toRate = exchangeRateRepository.findByCurrency("USD" + to.toUpperCase());

            if (fromRate != null && toRate != null) {
                BigDecimal exchangeRate = toRate.getRate().divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
                response.put("exchangeRate", exchangeRate);
            } else if (fromRate == null && from.equals("USD")) {
                response.put("exchangeRate", toRate.getRate());
            } else if (toRate == null && to.equals("USD")) {
                BigDecimal exchangeRate = BigDecimal.ONE.divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
                response.put("exchangeRate", exchangeRate);
            }
        }

        return ResponseEntity.ok(response);
    }


    /**
     * Get a list of exchange rates from a specified base currency.
     *
     * @param from The base currency code.
     * @return ResponseEntity containing exchange rates as a map with currency codes and rates.
     */
    @GetMapping("/rates")
    public ResponseEntity<Map<String, BigDecimal>> getExchangeRates(@RequestParam("from") String from) {
        if ("USD".equalsIgnoreCase(from)) {

            List<ExchangeRate> allExchangeRates = exchangeRateRepository.findAll();
            Map<String, BigDecimal> exchangeRates = new HashMap<>();

            for (ExchangeRate toExchangeRate : allExchangeRates) {
                exchangeRates.put(toExchangeRate.getCurrency(), toExchangeRate.getRate());
            }

            return ResponseEntity.ok(exchangeRates);
        } else {

            ExchangeRate fromRate = exchangeRateRepository.findByCurrency("USD" + from.toUpperCase());

            if (fromRate == null) {
                return ResponseEntity.badRequest().build();
            }

            Map<String, BigDecimal> exchangeRates = new HashMap<>();

            List<ExchangeRate> allExchangeRates = exchangeRateRepository.findAll();
            for (ExchangeRate toExchangeRate : allExchangeRates) {
                if (toExchangeRate.getCurrency().equals(from)) {
                    continue;
                }

                BigDecimal conversionRate = toExchangeRate.getRate().divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
                String toCurrency = toExchangeRate.getCurrency().replace("USD", from);

                exchangeRates.put(toCurrency, conversionRate);
            }

            return ResponseEntity.ok(exchangeRates);
        }
    }


    /**
     * Convert an amount from one currency to another.
     *
     * @param from   The source currency code.
     * @param to     The target currency code.
     * @param qty    The quantity amount to convert.
     * @return ResponseEntity containing the converted amount as "conversionResult" in a map.
     */
    @GetMapping("/convert")
    public ResponseEntity<Map<String, BigDecimal>> convertCurrency(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("qty") BigDecimal qty) {

        Map<String, BigDecimal> response = new HashMap<>();

        ExchangeRate fromRate = null;
        ExchangeRate toRate = null;

        if (from.toUpperCase().equals("USD")) {
            fromRate = new ExchangeRate();
            fromRate.setRate(BigDecimal.ONE);
        } else {
            fromRate = exchangeRateRepository.findByCurrency("USD" + from.toUpperCase());
        }

        if (to.toUpperCase().equals("USD")) {
            toRate = new ExchangeRate();
            toRate.setRate(BigDecimal.ONE);
        } else {
            toRate = exchangeRateRepository.findByCurrency("USD" + to.toUpperCase());
        }

        BigDecimal conversionRate = BigDecimal.ZERO;

        if (fromRate != null && toRate != null) {
            conversionRate = toRate.getRate().divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
        }

        BigDecimal convertedAmount = qty.multiply(conversionRate);

        response.put("conversionResult", convertedAmount);

        return ResponseEntity.ok(response);
    }

    /**
     * Convert an amount from one currency to multiple currencies.
     *
     * @param from         The source currency code.
     * @param toCurrencies List of target currency codes.
     * @param qty          The quantity amount to convert.
     * @return Total converted amount.
     */
    @GetMapping("/convert-multiple")
    public ResponseEntity<Map<String, BigDecimal>> convertMultipleCurrencies(
            @RequestParam("from") String from,
            @RequestParam("to") List<String> toCurrencies,
            @RequestParam("qty") BigDecimal qty) {

        Map<String, BigDecimal> response = new HashMap<>();
        System.out.println("Convert multiple called");

        BigDecimal total = BigDecimal.ZERO;

        if (from.equalsIgnoreCase("USD")) {
            System.out.println("inside if");
            for (String to : toCurrencies) {
                if (to.equalsIgnoreCase("USD")) {
                    continue;
                }
                System.out.println(to);
                ExchangeRate toRate = exchangeRateRepository.findByCurrency("USD" + to.toUpperCase());
                System.out.println("Iterating over" + toRate.getCurrency());
                if (toRate != null) {
                    System.out.println("is it not null");
                    BigDecimal conversionRate = toRate.getRate();
                    BigDecimal convertedAmount = qty.multiply(conversionRate);
                    response.put(to, convertedAmount);
                    total = total.add(convertedAmount);
                }
            }
        } else {
            System.out.println("inside else");
            for (String to : toCurrencies) {
                ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency("USD" + from.toUpperCase());
                System.out.println(exchangeRate.getCurrency());
                if (exchangeRate != null) {
                    total = total.add(qty.multiply(exchangeRate.getRate()));
                    response.put(to, total);
                }
            }
        }

        return ResponseEntity.ok(response);
    }


}