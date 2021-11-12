package com.wy.crl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wy.model.AccountChangeEvent;
import com.wy.service.AccountInfoService;

/**
 * 
 * 
 * @author 飞花梦影
 * @date 2021-11-12 11:07:55
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RestController
public class AccountInfoController {

	@Autowired
	private AccountInfoService accountInfoService;

	@GetMapping(value = "/transfer")
	public String transfer(@RequestParam("accountNo") String accountNo, @RequestParam("amount") Double amount) {
		// 创建一个事务id,作为消息内容发到mq
		String tx_no = UUID.randomUUID().toString();
		AccountChangeEvent accountChangeEvent = new AccountChangeEvent(accountNo, amount, tx_no);
		// 发送消息
		accountInfoService.sendUpdateAccountBalance(accountChangeEvent);
		return "转账成功";
	}
}