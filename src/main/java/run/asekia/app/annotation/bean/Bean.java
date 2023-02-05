package run.asekia.app.annotation.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @Shred} 是作用于类上的注解，这些类会被自动注入到 shredFactory 的容器中
 * @author Dioxide.CN
 * @date 2023/1/16 9:11
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String alias() default "";
}
