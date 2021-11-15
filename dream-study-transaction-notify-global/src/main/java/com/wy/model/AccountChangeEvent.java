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
public class AccountChangeEvent implements Serializable {

	private static final long serialVersionUID = -6810987579644799677L;

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