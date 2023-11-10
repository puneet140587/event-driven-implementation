package com.payment.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.ms.dto.CustomerOrder;
import com.payment.ms.dto.OrderEvent;
import com.payment.ms.entity.Payment;
import com.payment.ms.entity.PaymentRepository;

@Controller
public class PaymentController {
	@Autowired
	private PaymentRepository repository;

	@KafkaListener(topics = "orders-topic", groupId = "orders-group")
	public void processPayment(String event) throws JsonMappingException, JsonProcessingException {
		System.out.println("Recieved event for payment " + event);
		OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

		CustomerOrder order = orderEvent.getOrder();

		Payment payment = new Payment();
		payment.setAmount(order.getAmount());
		payment.setMode(order.getPaymentMode());
		payment.setOrderId(order.getOrderId());
		payment.setStatus("SUCCESS");
		repository.save(payment);
	}

}
