package io.mybatis.extra.mapper.optlock;

import io.mybatis.provider.EntityColumn;
import io.mybatis.provider.EntityField;
import io.mybatis.provider.EntityTable;
import io.mybatis.provider.EntityTableFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProxyOptlockEntityTableFactory implements EntityTableFactory {
  /**
   * 乐观锁自增 1
   */
  private final static String OPTLOCK_AUTO_INCR = " +1";

  /**
   * @return
   */
  @Override
  public int getOrder() {
    return EntityTableFactory.super.getOrder() + 200;
  }

  @Override
  public EntityTable createEntityTable(Class<?> entityClass, Chain chain) {
    EntityTable entityTable = chain.createEntityTable(entityClass);
    if (entityTable != null) {
      Boolean enabled = entityTable.getPropBoolean("optlock.column.enabled", true);
      if (enabled) {
        entityTable = proxy(entityTable);
      }
    }
    return entityTable;
  }

  /**
   * 动态代理 entityTable 方法
   *
   * @param entityTable 被代理对象
   */
  public EntityTable proxy(EntityTable entityTable) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(EntityTable.class);
    enhancer.setCallback(new EntityTableMethodInterceptor(entityTable));
    return (EntityTable) enhancer.create(new Class[]{Class.class}, new Object[]{entityTable.entityClass()});
  }

  public static class EntityTableMethodInterceptor implements MethodInterceptor {
    private final EntityTable        target;
    private       List<EntityColumn> proxyColumns = new ArrayList<>();

    public EntityTableMethodInterceptor(EntityTable target) {
      this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      // 有乐观锁字段，则加入
      if ("addColumn".equals(method.getName()) && objects.length == 1) {
        EntityColumn column = (EntityColumn) objects[0];
        EntityField field = column.field();
        if (field.isAnnotationPresent(Version.class) && (field.getType() == Long.class || field.getType() == Integer.class)) {
          // 防止乐观锁字段 被设置为不更新
          column.updatable(true);
          Enhancer enhancer = new Enhancer();
          enhancer.setSuperclass(EntityColumn.class);
          enhancer.setCallback(new EntityOptlockColumnMethodInterceptor(column));
          column = (EntityColumn) enhancer.create(new Class[]{EntityField.class}, new Object[]{column.field()});
          proxyColumns.add(column);
          objects[0] = column;
        }
      }
      // 有乐观锁字段，加入 where
      if ("idColumns".equals(method.getName()) && !proxyColumns.isEmpty()) {
        List<EntityColumn> columnList = (List<EntityColumn>) method.invoke(target, objects);
        if (null == columnList) {
          columnList = new ArrayList<>();
        }
        columnList.addAll(proxyColumns);
        return columnList;
      }
      return method.invoke(target, objects);
    }
  }

  public static class EntityOptlockColumnMethodInterceptor implements MethodInterceptor {
    private final EntityColumn target;
    private       boolean      flag = true;

    public EntityOptlockColumnMethodInterceptor(EntityColumn target) {
      this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      if ("columnEqualsProperty".equals(method.getName())) {
        // set 的时候调用
        if (flag) {
          flag = false;
          return
//              "<choose> " +
//              "<when test=\"@" + OptlockMapper.class.getName() + "@optlock('" + target.entityTable().table() + "')\">" +
//              target.column() + " = " + target.column() + OPTLOCK_AUTO_INCR +
//              "</when>" +
//              "<otherwise>" +
//              target.column() + " = " + target.column()  +
//              "</otherwise>" +
//              " </choose>";
              // 固定每次修改，加 1
                target.column() + " = " + target.column() + OPTLOCK_AUTO_INCR;
        }
        //where 的时候调用
        return "<choose> " +
            "<when test=\"@" + OptlockMapper.class.getName() + "@optlock('" + target.entityTable().table() + "')\">" +
            method.invoke(target, objects) +
            "</when>" +
            "<otherwise>" +
            "1 = 1" +
            "</otherwise>" +
            " </choose>";
      }
      return method.invoke(target, objects);
    }
  }


}
