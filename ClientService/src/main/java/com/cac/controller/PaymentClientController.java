package com.cac.controller;

import com.cac.model.Payment;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class PaymentClientController {

    private static final Logger logger = Logger.getLogger(PaymentClientController.class.getName());

    private final String backendUrl = "http://localhost:8082/api/payments";

    
    @GetMapping("/payments")
    public String getAllPayments(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(backendUrl + "/searchbypayment", String.class);
        logger.info("Backend JSON response: " + jsonResponse);
        try {
        	 Payment[] paymentArray = restTemplate.getForObject(backendUrl + "/searchbypayment", Payment[].class);
             List<Payment> payments = Arrays.asList(paymentArray);
                     model.addAttribute("payments", payments);
        } catch (Exception e) {
            logger.severe("Error fetching payments: " + e.getMessage());
            model.addAttribute("error", "Error fetching payments");
        }
        return "payments";
    }

    @RequestMapping("/create")
    public String showCreatePage(@RequestParam("billId") String billId, 
                                  @RequestParam("finalamount") double finalamount, 
                                  Model model) {
        model.addAttribute("billId", billId);
        model.addAttribute("finalamount", finalamount);
       
        return "createpayment";
        
    }
    
    @GetMapping("/viewpayments")
    public String viewpayments() {
        return "payments";
    }

   

    @GetMapping("/searchbypayment")
    public String searchPayments(
            @RequestParam(required = false) Integer billId,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentStatus,
            Model model) {
        try {
            logger.info("Payment Method: " + paymentMethod);
            logger.info("Payment Status: " + paymentStatus);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/searchbypayment");
            if (billId != null) {
                builder.queryParam("billId", billId);
            }
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                builder.queryParam("paymentMethod", paymentMethod);
            }
            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                builder.queryParam("paymentStatus", paymentStatus);
            }

            String queryUrl = builder.toUriString();
            logger.info("Query URL: " + queryUrl);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Payment[]> response = restTemplate.exchange(
                    queryUrl, 
                    HttpMethod.GET, 
                    null, 
                    Payment[].class
            );
            if (response.getBody() != null && response.getBody().length > 0) {
                model.addAttribute("payments", Arrays.asList(response.getBody()));
            } else {
                model.addAttribute("errorMessage", "No payments found matching the criteria.");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            logger.severe("Error searching payments: " + ex.getResponseBodyAsString());
            model.addAttribute("errorMessage", ex.getResponseBodyAsString());
        } catch (Exception e) {
            logger.severe("Unexpected error searching payments: " + e.getMessage());
            model.addAttribute("errorMessage", "An unexpected error occurred while searching payments.");
        }
        return "payments";
    }
    @GetMapping("/searchByPaymentDate")
    public String filterPaymentsByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        try {
            if ((startDate == null || startDate.isEmpty()) || (endDate == null || endDate.isEmpty())) {
                model.addAttribute("errorMessage", "Please provide both start and end dates.");
                return "payments";
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/searchByDate")
                    .queryParam("startDate", startDate)
                    .queryParam("endDate", endDate);

            String queryUrl = builder.toUriString();
            logger.info("Date Query URL: " + queryUrl);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Payment[]> response = restTemplate.exchange(
                    queryUrl, 
                    HttpMethod.GET, 
                    null, 
                    Payment[].class
            );
            if (response.getBody() != null && response.getBody().length > 0) {
                model.addAttribute("payments", Arrays.asList(response.getBody()));
            } else {
                model.addAttribute("errorMessage", "No payments found for the specified date range.");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            logger.severe("Error filtering payments by date: " + ex.getResponseBodyAsString());
            model.addAttribute("errorMessage", ex.getResponseBodyAsString());
        } catch (Exception e) {
            logger.severe("Unexpected error filtering payments by date: " + e.getMessage());
            model.addAttribute("errorMessage", "An unexpected error occurred while filtering payments by date.");
        }
        return "payments";
    }

}
