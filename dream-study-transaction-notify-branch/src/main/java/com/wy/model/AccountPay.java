package com.wy.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountPay implements Serializable {

	private static final long serialVersionUID = -8876327536133311943L;

	/**
	 * 事务号
	 */
	private String id;

	/**
	 * 账号
	 */
	private String accountNo;

	/**
	 * 变动金额
	 */
	private double payAmount;

	/**
	 * 充值结果
	 */
	private String result;
}