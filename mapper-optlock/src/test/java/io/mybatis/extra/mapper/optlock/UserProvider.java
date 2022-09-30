package io.mybatis.extra.mapper.optlock;

import io.mybatis.provider.EntityColumn;
import io.mybatis.provider.EntityTable;
import io.mybatis.provider.SqlScript;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.stream.Collectors;

/**
 * @author dengsd
 * @date 2022/9/30 9:06
 */
public class UserProvider {

  public static String updateByIdAndVersion(ProviderContext providerContext) {
    return SqlScript.caching(providerContext, new SqlScript() {
      @Override
      public String getSql(EntityTable entity) {
        String sql = "UPDATE " + entity.tableName()
            + " SET " + entity.updateColumns().stream().map(EntityColumn::columnEqualsProperty).collect(Collectors.joining(","))
            + where(() -> entity.idColumns().stream().map(EntityColumn::columnEqualsProperty).collect(Collectors.joining(" AND ")));
        System.out.println(sql);
        return sql;
      }
    });
  }
}
