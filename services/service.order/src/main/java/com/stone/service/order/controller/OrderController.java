package com.stone.service.order.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {
	private final static Logger logger = Logger.getLogger(OrderController.class);
	@Autowired
	private DiscoveryClient client;
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String index() {
		List<ServiceInstance> instances = client.getInstances("service.order");
		for (int i = 0; i < instances.size(); i++) {
			logger.info("/order,host:" + instances.get(i).getHost() + ",service_id:" + instances.get(i).getServiceId());
		}
		
		logger.info(restTemplate);
		logger.info(restTemplate.getForEntity("http://service.auth/auth", String.class));
		logger.info(restTemplate.getForEntity("http://SERVICE.AUTH/auth", String.class));
		
		logger.info(restTemplate.getForEntity("http://SERVICE.AUTH/auth", String.class).getBody());
		return "Hello World";
	}
}
