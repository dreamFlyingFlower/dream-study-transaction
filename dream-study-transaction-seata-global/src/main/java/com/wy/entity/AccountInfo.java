package com.wy.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo implements Serializable {

	private static final long serialVersionUID = -8961463493569001084L;

	private Long id;

	private String accountName;

	private String accountNo;

	private String accountPassword;

	private Double accountBalance;
}