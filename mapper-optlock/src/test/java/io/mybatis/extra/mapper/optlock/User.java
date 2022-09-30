package io.mybatis.extra.mapper.optlock;

import io.mybatis.provider.Entity;

import java.io.Serializable;

/**
 * @author dengsd
 * @date 2022/9/30 8:48
 */
@Entity.Table("user")
public class User implements Serializable {
  @Entity.Column(id = true)
  private Long id;
  @Entity.Column("name")
  private String name;
  @Version
  @Entity.Column("update_count")
  private Integer updateCount;

  public User(Long id, String name, Integer updateCount) {
    this.id = id;
    this.name = name;
    this.updateCount = updateCount;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getUpdateCount() {
    return updateCount;
  }

  public void setUpdateCount(Integer updateCount) {
    this.updateCount = updateCount;
  }
}
