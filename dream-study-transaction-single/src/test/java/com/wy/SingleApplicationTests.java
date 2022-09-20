package com.wy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wy.service.UserService;

@SpringBootTest
class SingleApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
		userService.test01();
	}
}