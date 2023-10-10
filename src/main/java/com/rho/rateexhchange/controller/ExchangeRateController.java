package com.rho.rateexhchange.controller;

import com.rho.rateexhchange.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
@Tag(name = "Exchange Rate API", description = "Service that provides currency exchange rate information and allows users to perform currency conversion operations")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Get the exchange rate between two currencies.
     *
     * @param from The source currency code.
     * @param to   The target currency code.
     * @return ResponseEntity containing the exchange rate or an error response.
     */
    @Operation(
            summary = "Get the exchange rate between two currencies",
            description = "Get the exchange rate between two currencies specifying them. The response is the rate between the specified currencies.")
    @GetMapping("/exchange-rate")
    public ResponseEntity<Map<String, Object>> getExchangeRate(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        try {
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(from, to);
            Map<String, Object> response = new HashMap<>();
            response.put("exchangeRate", exchangeRate);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get a map of exchange rates for a given base currency.
     *
     * @param from The base currency code.
     * @return ResponseEntity containing the exchange rates or an error response.
     */
    @Operation(
            summary = "Get a list of exchange rates from a currency",
            description = "Get a list of exchange rates from one currency. The response is a list of rates based on the specified currency.")
    @GetMapping("/rates")
    public ResponseEntity<Map<String, Object>> getExchangeRates(
            @RequestParam("from") String from) {
        try {
            Map<String, BigDecimal> exchangeRates = exchangeRateService.getExchangeRates(from);
            Map<String, Object> response = new HashMap<>(exchangeRates);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Convert an amount from one currency to another.
     *
     * @param from The source currency code.
     * @param to   The target currency code.
     * @param qty  The quantity amount to convert.
     * @return ResponseEntity containing the converted amount or an error response.
     */
    @Operation(
            summary = "Convert an amount from one currency to another",
            description = "Get the converted amount between two currencies. The response is the conversion result of the amount of the base rate specified.")
    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertCurrency(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("qty") BigDecimal qty) {
        try {
            BigDecimal convertedAmount = exchangeRateService.convertCurrency(from, to, qty);
            Map<String, Object> response = new HashMap<>();
            response.put("conversionResult", convertedAmount);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Convert an amount from one currency to multiple currencies.
     *
     * @param from         The source currency code.
     * @param toCurrencies List of target currency codes.
     * @param qty          The quantity amount to convert.
     * @return ResponseEntity containing the converted amounts or an error response.
     */
    @Operation(
            summary = "Convert an amount from one currency to many",
            description = "Get a list of the converted amount between a base currency to many. The response is a list of the conversion result of the amount of the base rate specified.")
    @GetMapping("/convert-multiple")
    public ResponseEntity<Map<String, Object>> convertMultipleCurrencies(
            @RequestParam("from") String from,
            @RequestParam("to") List<String> toCurrencies,
            @RequestParam("qty") BigDecimal qty) {
        try {
            Map<String, BigDecimal> conversionResults = exchangeRateService.convertMultipleCurrencies(from, toCurrencies, qty);
            Map<String, Object> response = new HashMap<>(conversionResults);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
