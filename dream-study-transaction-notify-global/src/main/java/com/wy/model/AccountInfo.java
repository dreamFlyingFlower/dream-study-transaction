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
public class AccountInfo implements Serializable {

	private static final long serialVersionUID = -2246533570185375130L;

	private Long id;

	private String accountName;

	private String accountNo;

	private String accountPassword;

	private Double accountBalance;
}