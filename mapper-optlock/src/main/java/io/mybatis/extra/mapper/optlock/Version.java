package io.mybatis.extra.mapper.optlock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 乐观锁注解
 * @author dengsd
 * @date 2022/9/29 15:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public  @interface Version {
}
