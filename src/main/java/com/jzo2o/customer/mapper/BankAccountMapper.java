package com.jzo2o.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.customer.model.domain.BankAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 银行账户 Mapper 接口
 * </p>
 *
 * @author jzo2o
 * @since 2026-05-23
 */
@Mapper
public interface BankAccountMapper extends BaseMapper<BankAccount> {
    // 💡 提示：MyBatis-Plus 自带的 selectById、insert、updateById 已经完全够我们前面的 Service 层使用了。
    // 如果以后有极其复杂的、需要手写 SQL 的多表联查，再在这里定义方法并去 XML 里写 SQL 即可。
}