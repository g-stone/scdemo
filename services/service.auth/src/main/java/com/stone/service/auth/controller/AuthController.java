package com.stone.service.auth.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	private final static Logger logger = Logger.getLogger(AuthController.class);
	@Autowired
	private DiscoveryClient client;
	
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public String index() {
		List<ServiceInstance> instances = client.getInstances("service.auth");
		for (int i = 0; i < instances.size(); i++) {
			logger.info("/auth,host:" + instances.get(i).getHost() + ",service_id:" + instances.get(i).getServiceId());
		}
		return "Hello World";
	}
}
