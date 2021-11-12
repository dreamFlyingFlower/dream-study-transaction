package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo implements Serializable {

	private static final long serialVersionUID = 8691617672742005130L;

	private Long id;

	private String accountName;

	private String accountNo;

	private String accountPassword;

	private Double accountBalance;
}