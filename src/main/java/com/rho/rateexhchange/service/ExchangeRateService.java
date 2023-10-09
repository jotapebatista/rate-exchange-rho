package com.rho.rateexhchange.service;

import com.rho.rateexhchange.model.ExchangeRate;
import com.rho.rateexhchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;

    String BASE_URL = "http://api.exchangerate.host/live";
    String API_KEY = "?access_key=7f2b0b6ec365312e9911324ada242079";

    @Autowired
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Scheduled(fixedRate = 600000)
    public void fetchAndStore() {
        try {

            RestTemplate restTemplate = new RestTemplate();

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
}
