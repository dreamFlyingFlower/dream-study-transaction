package com.wy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountInfoMapper {

	@Update("update account_info set account_balance = account_balance + #{amount} where account_no = #{accountNo}")
	int updateAccountBalance(@Param("accountNo") String accountNo, @Param("amount") Double amount);
}