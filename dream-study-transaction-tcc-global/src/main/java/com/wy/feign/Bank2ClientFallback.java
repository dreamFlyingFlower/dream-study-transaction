package com.wy.feign;

import org.springframework.stereotype.Component;

@Component
public class Bank2ClientFallback implements Bank2Client {

	@Override
	public Boolean transfer(Double amount) {
		return false;
	}
}