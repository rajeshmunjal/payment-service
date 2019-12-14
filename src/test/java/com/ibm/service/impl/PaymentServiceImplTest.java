package com.ibm.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.ibm.model.CreditCardInfo;
import com.ibm.model.Order;
import com.ibm.model.Payment;
import com.ibm.repository.OrderRepository;
import com.ibm.repository.PaymentRepository;
import com.ibm.service.impl.PaymentServiceImpl;

import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {
    private final static String ORDER_ID = "12345";
    private final static int ORDER_TOTAL_PRICE = 58;
    private final static String ORDER_COMPLETE_UPDATER = "http://order-complete-updater";

    private RestTemplate restTemplate;
    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;
    private PaymentServiceImpl paymentService;
    private Payment payment;
    private CreditCardInfo creditCardInfo;
    private Order order;

    @Before
    public void setMock() {
        restTemplate = mock(RestTemplate.class);
        orderRepository = mock(OrderRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentService = new PaymentServiceImpl(restTemplate, orderRepository, paymentRepository);

        payment = new Payment();
        payment.setAmount(ORDER_TOTAL_PRICE);
        payment.setOrderId(ORDER_ID);
        creditCardInfo = new CreditCardInfo();
        payment.setCreditCardInfo(creditCardInfo);
        order = new Order();
        order.setId(ORDER_ID);
        order.setTotalPrice(ORDER_TOTAL_PRICE);
    }

    @Test
    public void processPaymentSuccess() {
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(order);

        paymentService.processPayment(payment);

        verify(restTemplate).postForLocation(ORDER_COMPLETE_UPDATER + "/api/orders", order);

    }

    @Test
    public void processPaymentFail_whenAmountMisMatch() {
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(order);
        payment.setAmount(ORDER_TOTAL_PRICE + 10);

        paymentService.processPayment(payment);

        verify(restTemplate).postForLocation(ORDER_COMPLETE_UPDATER + "/api/errors", "Payment amount: "
                + payment.getAmount() + " doesn't match with order price: " + ORDER_TOTAL_PRICE + ", orderId = " + ORDER_ID);
    }

    @Test
    public void processPaymentFail_whenOrderNotFound() {
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(null);

        paymentService.processPayment(payment);

        verify(restTemplate).postForLocation(ORDER_COMPLETE_UPDATER
                + "/api/errors", "Failed to retrieve order for orderId: " + ORDER_ID);
    }
}
