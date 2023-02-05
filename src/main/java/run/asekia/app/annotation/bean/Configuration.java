package run.asekia.app.annotation.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>{@code @Configuration} 作用于类上是bean类型的一个变种，这些类会被实例化并自动装配至configuration工厂中，值 <code>extendable</code> 用来标注该配置类能否被继承重写。</p>
 * <p>Configuration类不能被 {@code @Autowired} 注入和使用，所有Configuration类会在bean工厂装配完成后被代理工厂执行完成配置。Configuration类遵循双亲委派原则，被允许的重写配置类会覆盖父配置类进行加载。</p>
 * @author Dioxide.CN
 * @date 2023/1/16 19:10
 * @since 1.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
    boolean extendable() default true;
}
