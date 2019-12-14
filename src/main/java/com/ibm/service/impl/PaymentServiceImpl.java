package com.ibm.service.impl;

import com.esotericsoftware.minlog.Log;
import com.ibm.model.CreditCardInfo;
import com.ibm.model.Order;
import com.ibm.model.Payment;
import com.ibm.repository.OrderRepository;
import com.ibm.repository.PaymentRepository;
import com.ibm.service.PaymentService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Random;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    private RestTemplate restTemplate;
    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;


    @Autowired
    public PaymentServiceImpl(RestTemplate restTemplate, OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }


    @Override
    public void processPayment(Payment payment) {
        System.out.println("ProcessPayment called()");
        try
        { 	
        payment = paymentRepository.save(payment);
        }
        catch (Exception e) {

            // generic exception handling
            e.printStackTrace();
        }
        String orderCompleteUpdater = "http://order-complete-updater";
        String orderId = payment.getOrderId();
        if (orderId == null) {
            sendErrorMessage("Missing orderId in payment");
        }
        Order order = orderRepository.findOrderById(orderId);
        if (order != null && validateCreditCardInfo(payment.getCreditCardInfo())) {
            if (order.getTotalPrice() != payment.getAmount()) {
                String errorMessage = "Payment amount: " + payment.getAmount() + " doesn't match with order price: " +
                        order.getTotalPrice() + ", orderId = " + orderId;
                sendErrorMessage(errorMessage);

            } else {
                order.setPaymentId(payment.getId());
                long deliveryTime = getDeliveryTime();
                order.setDeliveryTime(deliveryTime);
                orderRepository.save(order);
                restTemplate.postForLocation(orderCompleteUpdater + "/api/orders", order);

            }
        } else {
            String errorMessage = order == null ? "Failed to retrieve order for orderId: " + orderId
                    : "Invalid credit card information for orderId: " + orderId;
            sendErrorMessage(errorMessage);

        }
    }



    private boolean validateCreditCardInfo(CreditCardInfo creditCardInfo) {
        return true;
    }

    private long getDeliveryTime() {
        // randomly generate delivery estimation period, range: 5 ~ 1 hour
        Random random = new Random();
        int randomPeriod = 5 + random.nextInt(60 - 5 + 1);
        return System.currentTimeMillis() + randomPeriod * 60 * 1000;
    }

    private void sendErrorMessage(String errorMessage) {
        Log.warn(errorMessage);
        String orderCompleteUpdater = "http://order-complete-updater";
        restTemplate.postForLocation(orderCompleteUpdater + "/api/errors", errorMessage);
    }
}
