package com.rho.rateexhchange;

import com.rho.rateexhchange.controller.ExchangeRateController;
import com.rho.rateexhchange.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ExchangeRateControllerTest {

    @InjectMocks
    private ExchangeRateController controller;

    @Mock
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetExchangeRate() {

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(1.2));

        ResponseEntity<Map<String, Object>> response = controller.getExchangeRate("USD", "EUR");

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("exchangeRate"));
        assertEquals(BigDecimal.valueOf(1.2), responseBody.get("exchangeRate"));
    }

    @Test
    public void testGetExchangeRates() {

        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        exchangeRates.put("EUR", BigDecimal.valueOf(1.2));
        exchangeRates.put("GBP", BigDecimal.valueOf(1.4));
        when(exchangeRateService.getExchangeRates("USD")).thenReturn(exchangeRates);

        ResponseEntity<Map<String, Object>> response = controller.getExchangeRates("USD");

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("EUR"));
        assertTrue(responseBody.containsKey("GBP"));
        assertEquals(BigDecimal.valueOf(1.2), responseBody.get("EUR"));
        assertEquals(BigDecimal.valueOf(1.4), responseBody.get("GBP"));
    }

    @Test
    public void testConvertCurrency() {

        when(exchangeRateService.convertCurrency("USD", "EUR", BigDecimal.valueOf(100))).thenReturn(BigDecimal.valueOf(120));

        ResponseEntity<Map<String, Object>> response = controller.convertCurrency("USD", "EUR", BigDecimal.valueOf(100));

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("conversionResult"));
        assertEquals(BigDecimal.valueOf(120), responseBody.get("conversionResult"));
    }

    @Test
    public void testConvertMultipleCurrencies() {

        List<String> toCurrencies = Arrays.asList("GBP", "BRL", "USD");
        Map<String, BigDecimal> conversionResults = new HashMap<>();
        conversionResults.put("GBP", BigDecimal.valueOf(140));
        conversionResults.put("BRL", BigDecimal.valueOf(200));
        conversionResults.put("USD", BigDecimal.valueOf(120));
        when(exchangeRateService.convertMultipleCurrencies("USD", toCurrencies, BigDecimal.valueOf(100))).thenReturn(conversionResults);

        ResponseEntity<Map<String, Object>> response = controller.convertMultipleCurrencies("USD", toCurrencies, BigDecimal.valueOf(100));

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("GBP"));
        assertTrue(responseBody.containsKey("BRL"));
        assertTrue(responseBody.containsKey("USD"));
        assertEquals(BigDecimal.valueOf(140), responseBody.get("GBP"));
        assertEquals(BigDecimal.valueOf(200), responseBody.get("BRL"));
        assertEquals(BigDecimal.valueOf(120), responseBody.get("USD"));
    }

}
