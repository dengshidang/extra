package io.mybatis.extra.mapper.optlock.test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

public class BaseTest {
  private static SqlSessionFactory sqlSessionFactory;

  @BeforeClass
  public static void init() {
    try {
      Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      reader.close();

      //创建数据库
      try (SqlSession session = sqlSessionFactory.openSession()) {
        Connection conn = session.getConnection();
        reader = Resources.getResourceAsReader("testdb.sql");
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setLogWriter(null);
        runner.runScript(reader);
        reader.close();
      }
    } catch (IOException ignore) {
    }
  }

  public SqlSession getSqlSession() {
    return sqlSessionFactory.openSession();
  }

}
