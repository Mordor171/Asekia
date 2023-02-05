package run.asekia.app.annotation.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>{@code @Component} 作用于类上是bean类型的一个变种，被该注解修饰的类会在bean工厂中被装配至bean容器中，并通过 {@code @Autowired} 实现自动装配。</p>
 * <p>组件注解一般作用于工具类</p>
 * @author Dioxide.CN
 * @date 2023/1/16 14:29
 * @since 1.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String alias() default "";
}
