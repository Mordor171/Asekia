package run.asekia.app.annotation.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @After} 是作用在方法上的注解，这些方法将会在动态代理工厂调用后执行
 * @author Dioxide.CN
 * @date 2023/1/16 11:32
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
}
