package com.wy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AccountInfoMapper {

	// 更新账户
	@Update("UPDATE account_info SET account_balance = account_balance + #{amount} WHERE account_no = #{accountNo}")
	int updateAccountBalance(@Param("accountNo") String accountNo, @Param("amount") Double amount);
}