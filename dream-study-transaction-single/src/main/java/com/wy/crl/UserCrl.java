package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.service.UserService;

/**
 * 
 * 
 * @author 飞花梦影
 * @date 2021-11-12 11:07:55
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RestController
@RequestMapping("user")
public class UserCrl {

	@Autowired
	private UserService userService;

	@GetMapping("test01")
	public void test01() {
		userService.test01();
	}
}