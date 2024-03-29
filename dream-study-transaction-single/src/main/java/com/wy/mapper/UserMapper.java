package com.wy.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.wy.model.User;

@Mapper
public interface UserMapper {

	@Update("update account_info set account_balance=account_balance+#{amount} where account_no=#{accountNo}")
	int updateAccountBalance(@Param("accountNo") String accountNo, @Param("amount") Double amount);

	@Select("select * from account_info where where account_no=#{accountNo}")
	User findByIdAccountNo(@Param("accountNo") String accountNo);

	@Select("select count(1) from de_duplication where tx_no = #{txNo}")
	int isExistTx(String txNo);

	@Insert("insert into de_duplication values(#{txNo},now());")
	int addTx(String txNo);
}