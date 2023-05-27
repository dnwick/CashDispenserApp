package com.atgov.cash.dispenser.resource;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CashInventoryResourceIT {

    private static final Logger log = LogManager.getLogger(CashInventoryResourceIT.class);
    private static final String GENERIC_CASH_INVENTORY_URL = "/v1/cash-inventory";
    private static final String GENERIC_CASH_INVENTORY_BILLS_URL = "/v1/cash-inventory/bills";
    private static final String GENERIC_CASH_INVENTORY_TRANSACTIONS_URL = "/v1/cash-inventory/transactions";

    //todo Need to write more test cases for cases like,
    // - Remove number of bills of one type and check the values
    // - validate for empty and other corner cases for all user inputs
    // - calling add or remove patch requests to update cash inventory while not initialized first

    @Autowired
    private TestRestTemplate template;

    @Test
    @Order(1)
    public void initializeCashDispenser_basicScenario() throws JSONException {
        //init configuration
        HttpClient httpClient = HttpClientBuilder.create().build();
        template.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        //run initial tests
        log.info("=======Testing initializeCashDispenser_basicScenario========");

        ResponseEntity<String> responseEntity = initCashDispenser();
        String expectedResponse =
                " { \"bills\": { \"TWENTY\": 400, \"FIFTY\": 200}, \"totalAmount\": 18000, " +
                        "\"currencyCode\": \"AUD\", \"transactions\": []} ";

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    private ResponseEntity<String> initCashDispenser() {
        String requestBody = " { \"currencyCode\": \"AUD\", \"bills\": {\"FIFTY\": 200,\"TWENTY\": 400}}" ;
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        return template.exchange(
                GENERIC_CASH_INVENTORY_URL, HttpMethod.POST, httpEntity, String.class);
    }

    @Test
    public void initializeCashDispenser_reinitializeErrorScenario() throws JSONException {
        log.info("=======Testing initializeCashDispenser_reinitializeErrorScenario========");

        //initialize again
        ResponseEntity<String> responseEntity = initCashDispenser();
        String expectedResponse = "{\"errors\":[\"Cash Dispenser already initialized.Cannot run reinitialization." +
                "Use patch api to add/remove bills.\"]}";
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), false);
    }

    @Test
    @Order(2)
    public void retrieveAllCash_basicScenario() throws JSONException {
        log.info("=========Testing retrieveAllCash_basicScenario========");
        ResponseEntity<String> responseEntity = template.getForEntity(GENERIC_CASH_INVENTORY_BILLS_URL, String.class);
        String expectedResponse =
                "{ \"TWENTY\": 400, \"FIFTY\": 200}";
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), false);
    }

    @Test
    @Order(3)
    public void addAllBillsToCashDispenser_basicScenario() throws JSONException {
        log.info("=========Testing addBillsToCashDispenser_basicScenario========");
        String requestBody = " { \"key\": \"bills\", " +
                "\"operation\": \"add\", \"value\": {\"FIFTY\": 150,\"TWENTY\": 200}}" ;

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json-patch+json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_URL, HttpMethod.PATCH, httpEntity, String.class);
        String expectedResponse =
                "{ \"TWENTY\": 600, \"FIFTY\": 350}";

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(4)
    public void removeAllBillsFromCashDispenser_basicScenario() throws JSONException {
        log.info("=========Testing removeBillsFromCashDispenser_basicScenario========");
        String requestBody = " { \"key\": \"bills\", \"operation\": \"remove\", " +
                "\"value\": {\"FIFTY\": 100,\"TWENTY\": 50}}" ;

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json-patch+json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_URL, HttpMethod.PATCH, httpEntity, String.class);
        String expectedResponse =
                "{ \"TWENTY\": 550, \"FIFTY\": 250}";

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(6)
    public void addOnly50BillsToCashDispenser_basicScenario() throws JSONException {
        log.info("=========Testing addOnly50BillsToCashDispenser_basicScenario========");
        String requestBody = " { \"key\": \"bills\", \"operation\": \"add\", \"value\": {\"FIFTY\": 320}}" ;

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json-patch+json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_URL, HttpMethod.PATCH, httpEntity, String.class);
        String expectedResponse =
                "{ \"TWENTY\": 550, \"FIFTY\": 570}";

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(7)
    public void addAllBillsToCashDispenser_withWrongContentType() throws JSONException {
        log.info("=========Testing addBillsToCashDispenser_basicScenario========");
        String requestBody = " { \"key\": \"bills\", \"operation\": \"add\", " +
                "\"value\": {\"FIFTY\": 150,\"TWENTY\": 200}}" ;

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_URL, HttpMethod.PATCH, httpEntity, String.class);
        String expectedResponse =
                "{\"errors\":[\"Unsupported media type: application/json. Supported media types are: " +
                        "[application/json-patch+json]\"]}";

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(8)
    public void doTransactionWithCashDispenser_basicScenario() throws JSONException {
        log.info("=========Testing doTransactionWithCashDispenser_basicScenario========");
        String requestBody = "{ \"amount\": \"590\"}}";

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_TRANSACTIONS_URL, HttpMethod.POST, httpEntity, String.class);
        String expectedResponse =
                "{ \"TWENTY\": 2, \"FIFTY\": 11}";

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json",  responseEntity.getHeaders().get("content-type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(9)
    public void doTransactionWithCashDispenser_higherThanLimitPerTransaction() throws JSONException {
        log.info("=========Testing doTransactionWithCashDispenser_higherThanLimitPerTransaction========");
        String requestBody = "{ \"amount\": \"12000\"}}";

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_TRANSACTIONS_URL, HttpMethod.POST, httpEntity, String.class);
        String expectedResponse =
                "{\"errors\":[\"Transaction limit reached.Requested amount 12000 is higher than the limit per " +
                        "transaction 10000\"]}";

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(10)
    public void doTransactionWithCashDispenser_unDispensableAmount() throws JSONException {
        log.info("=========Testing doTransactionWithCashDispenser_unDispensableAmount========");
        String requestBody = "{ \"amount\": \"110\"}}";

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_TRANSACTIONS_URL, HttpMethod.POST, httpEntity, String.class);
        String expectedResponse =
                "{\"errors\":[\"Unable to dispense the requested amount 110 with legal " +
                        "combinations of bill types FIFTY and TWENTY.\"]}";

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }

    @Test
    @Order(11)
    public void doTransactionWithCashDispenser_withLowerThanMinimumAmount() throws JSONException {
        log.info("=========Testing doTransactionWithCashDispenser_unDispensableAmount========");
        String requestBody = "{ \"amount\": \"10\"}}";

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> responseEntity = template.exchange(
                GENERIC_CASH_INVENTORY_TRANSACTIONS_URL, HttpMethod.POST, httpEntity, String.class);
        String expectedResponse =
                "{\"errors\":[\"Invalid amount: transaction amount should be greater than or equal to 20\"]}";

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), true);
    }


}
