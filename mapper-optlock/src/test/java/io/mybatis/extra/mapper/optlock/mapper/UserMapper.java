package io.mybatis.extra.mapper.optlock.mapper;


import io.mybatis.extra.mapper.optlock.User;
import io.mybatis.extra.mapper.optlock.UserProvider;
import io.mybatis.provider.Caching;
import io.mybatis.provider.EntityInfoMapper;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author dengsd
 * @date 2022/9/30 8:51
 */
public interface UserMapper extends EntityInfoMapper<User> {
  @Lang(Caching.class)
  @UpdateProvider(type = UserProvider.class, method = "updateByIdAndVersion")
   int updateByIdAndVersion(User user);
}
