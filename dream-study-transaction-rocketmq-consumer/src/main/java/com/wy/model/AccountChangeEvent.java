package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountChangeEvent implements Serializable {

	private static final long serialVersionUID = -3619645235032099305L;

	/**
	 * 账号
	 */
	private String accountNo;

	/**
	 * 变动金额
	 */
	private double amount;

	/**
	 * 事务号
	 */
	private String txNo;
}