package com.rho.rateexhchange.service;

import com.rho.rateexhchange.model.ExchangeRate;
import com.rho.rateexhchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateService {
    String BASE_URL = "http://api.exchangerate.host/live";
    String API_KEY = "?access_key=7f2b0b6ec365312e9911324ada242079";
    private String baseCurrency = "USD";
    private final ExchangeRateRepository exchangeRateRepository;
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    /**
     * Scheduled method to fetch exchange rates from an external API and store them in the database.
     */
    @Scheduled(fixedRate = 600000)
    public void fetchAndStore() {
        try {

            ExchangeRateResponse response = restTemplate.getForObject(BASE_URL + API_KEY, ExchangeRateResponse.class);
            if (response != null && response.getQuotes() != null) {
                response.getQuotes().forEach((currency, rate) -> {
                    ExchangeRate exchangeRate = new ExchangeRate();
                    exchangeRate.setCurrency(currency);
                    exchangeRate.setRate(rate);
                    exchangeRateRepository.saveAndFlush(exchangeRate);
                });
            }

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Get the exchange rate between two currencies.
     *
     * @param from The source currency code.
     * @param to   The target currency code.
     * @return The exchange rate.
     * @throws IllegalArgumentException if the provided currency codes are invalid.
     */
    public BigDecimal getExchangeRate(String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return BigDecimal.ONE;
        }

        ExchangeRate fromRate = getExchangeRateByCurrency(from);
        ExchangeRate toRate = getExchangeRateByCurrency(to);

        if (fromRate != null && toRate != null) {
            return toRate.getRate().divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
        } else if (fromRate == null && from.equals(baseCurrency)) {
            return toRate.getRate();
        } else if (toRate == null && to.equals(baseCurrency)) {
            return BigDecimal.ONE.divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
        } else {
            throw new IllegalArgumentException("Invalid currency codes provided.");
        }
    }

    /**
     * Get a list of exchange rates for a given currency.
     *
     * @param from The base currency code.
     * @return A list of currency codes to exchange rates.
     * @throws IllegalArgumentException if the provided base currency is invalid.
     */
    public Map<String, BigDecimal> getExchangeRates(String from) {
        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        List<ExchangeRate> allExchangeRates = exchangeRateRepository.findAll();

        if (from.equalsIgnoreCase(baseCurrency)) {
            for (ExchangeRate exchangeRate : allExchangeRates) {
                exchangeRates.put(exchangeRate.getCurrency(), exchangeRate.getRate());
            }
        } else {
            ExchangeRate fromRate = getExchangeRateByCurrency(from);

            if (fromRate == null) {
                throw new IllegalArgumentException("Invalid base currency provided.");
            }

            for (ExchangeRate toExchangeRate : allExchangeRates) {
                if (toExchangeRate.getCurrency().equals(from)) {
                    continue;
                }

                BigDecimal conversionRate = toExchangeRate.getRate().divide(fromRate.getRate(), 6, RoundingMode.HALF_UP);
                String toCurrency = toExchangeRate.getCurrency().replace(baseCurrency, from);

                exchangeRates.put(toCurrency, conversionRate);
            }
        }

        return exchangeRates;
    }

    /**
     * Convert an amount from one currency to another.
     *
     * @param from The source currency code.
     * @param to   The target currency code.
     * @param qty  The quantity amount to convert.
     * @return The converted amount.
     * @throws IllegalArgumentException if the provided currency codes are invalid.
     */
    public BigDecimal convertCurrency(String from, String to, BigDecimal qty) {
        BigDecimal conversionRate = getExchangeRate(from, to);
        return qty.multiply(conversionRate);
    }


    /**
     * Convert an amount from one currency to multiple currencies.
     *
     * @param from         The source currency code.
     * @param toCurrencies List of target currency codes.
     * @param qty          The quantity amount to convert.
     * @return A map of currency codes to converted amounts.
     * @throws IllegalArgumentException if the provided currency codes are invalid.
     */
    public Map<String, BigDecimal> convertMultipleCurrencies(String from, List<String> toCurrencies, BigDecimal qty) {
        Map<String, BigDecimal> conversionResults = new HashMap<>();

        for (String to : toCurrencies) {
            BigDecimal convertedAmount = convertCurrency(from, to, qty);
            conversionResults.put(to, convertedAmount);
        }

        return conversionResults;
    }

    // Helper method to retrieve exchange rates by currency code
    private ExchangeRate getExchangeRateByCurrency(String currency) {
        if (baseCurrency.equalsIgnoreCase(currency)) {
            ExchangeRate baseRate = new ExchangeRate();
            baseRate.setRate(BigDecimal.ONE);
            return baseRate;
        }

        String currencyWithUSD = baseCurrency + currency.toUpperCase();
        return exchangeRateRepository.findByCurrency(currencyWithUSD);
    }
}

