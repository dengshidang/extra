package io.mybatis.extra.mapper.optlock;

import io.mybatis.extra.mapper.optlock.mapper.UserMapper;
import io.mybatis.extra.mapper.optlock.test.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author dengsd
 * @date 2022/9/30 8:49
 */

public class UserUpdateVersionTest extends BaseTest {
  @Test
  public void testUserUpdateVersionSuccess(){
    try (SqlSession sqlSession = getSqlSession()) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      User user = new User(1L,"测试",0);
      int update = OptlockMapper.lock(() -> userMapper.entityTable().table(), () -> userMapper.updateByIdAndVersion(user));
      Assert.assertEquals(1, update);
      user.setUpdateCount(1);
       update = OptlockMapper.lock(() -> userMapper.entityTable().table(), () -> userMapper.updateByIdAndVersion(user));
      Assert.assertEquals(0, update);
    }
  }
  @Test
  public void testUserUpdateVersionFail(){
    try (SqlSession sqlSession = getSqlSession()) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      User user = new User(1L,"测试",1);
      int update = OptlockMapper.lock(() -> userMapper.entityTable().table(), () -> userMapper.updateByIdAndVersion(user));
      Assert.assertEquals(0, update);
    }
  }
  @Test
  public void testUserUpdate(){
    try (SqlSession sqlSession = getSqlSession()) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      User user = new User(1L,"测试",1);
      int update = userMapper.updateByIdAndVersion(user);
      Assert.assertEquals(1, update);
    }
  }
}
