package io.mybatis.extra.mapper.optlock;

import io.mybatis.provider.EntityTable;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author dengsd
 * @date 2022/9/30 10:20
 */
public class OptlockMapper {
  public static ThreadLocal<Map<String, Boolean>> OPT_LOCK = new ThreadLocal<>();

  /**
   * 乐观锁 方式 更新数据，
   * 调用前提：当前表字段 有这个{@link io.mybatis.extra.mapper.optlock.Version}注解
   * @param table
   * @param mapper
   * @return
   */
  public static int lock(Supplier<String> table,Supplier<Integer> mapper){
    // 开启乐观锁
    String tb = table.get();
    start(tb);
    int update = mapper.get();
    //关闭乐观锁
    end(tb);
    return update;
  }
  /**
   * 开启乐观锁
   *
   * @param table
   */
  private static void start(String table) {
    Map<String, Boolean> optmap = OPT_LOCK.get();
    if (null == optmap) {
      optmap = new HashMap<>();
    }
    optmap.put(table, true);
    OPT_LOCK.set(optmap);
  }

  /**
   * 关闭乐观锁
   *
   * @param table
   */
  private static void end(String table) {
    Map<String, Boolean> optmap = OPT_LOCK.get();
    if (null != optmap) {
      optmap.put(table, false);
      OPT_LOCK.set(optmap);
    }
  }

  public static boolean optlock(String table) {
    Map<String, Boolean> optmap = OPT_LOCK.get();
    if (null == optmap || !optmap.containsKey(table)) return false;
    return optmap.get(table);
  }
}
