package cn.itz.cloud;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.junit.Test;

import java.time.Duration;

/**
 * @PackageName: cn.itz.cloud
 * @ClassName: Resilience4jTest
 * @Author: codeZhang
 * @DateTime: 2021/1/5 10:25
 * @Version 1.0
 */
public class Resilience4jTest {

  /**
   * 一个正常执行的例子
   */
  @Test
  public void test(){
    //获取一个CircuitBreakerRegistry实例，可以调用ofDefaults获取一个CircuitBreakerRegistry，也可以自定义属性
    CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        //故障率阈值百分比，超过这个阈值，断路器就会打开
        .failureRateThreshold(50)
        //断路器保持打开的时间，在到达设置的时间之后，断路器会进入到half open状态
        .waitDurationInOpenState(Duration.ofMillis(1000))
        //当断路器处于half open状态时，环形缓冲区的大小
        .ringBufferSizeInHalfOpenState(2)
        .ringBufferSizeInClosedState(2)
        .build();
    CircuitBreakerRegistry r1 = CircuitBreakerRegistry.of(config);
    CircuitBreaker cb1 = r1.circuitBreaker("zhang");
    CircuitBreaker cb2 = r1.circuitBreaker("zhang", config);
    CheckedFunction0<String> supplier = CircuitBreaker.decorateCheckedSupplier(cb1,() -> "he11o resilience4");
    Try<String> result = Try.of(supplier).map(v -> v +"he11o world");
    System.out.println(result.isSuccess());
    System.out.println(result.get());
  }

  @Test
  public void test2(){
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        //故障率阈值百分比，超过这个阈值，断路器就会打开
        .failureRateThreshold(50)
        //断路器保持打开的时间，在到达设置的时间之后，断路器会进入到half open状态
        .waitDurationInOpenState(Duration.ofMillis(1000))
        //当断路器处于half open状态时，环形缓冲区的大小
        .ringBufferSizeInClosedState(2)
        .build();
    CircuitBreakerRegistry r1 = CircuitBreakerRegistry.of(config);
    CircuitBreaker cb1 = r1.circuitBreaker("zhang");
    System.out.println(cb1.getState());  //获取断路器的一个状态
    cb1.onError(0,new RuntimeException());
    System.out.println(cb1.getState());  //获取断路器的一个状态
    cb1.onError(0,new RuntimeException());
    System.out.println(cb1.getState());  //获取断路器的一个状态
    CheckedFunction0<String> supplier = CircuitBreaker.decorateCheckedSupplier(cb1,() -> "he11o resilience4");
    Try<String> result = Try.of(supplier).map(v -> v +"he11o world");
    System.out.println(result.isSuccess());
    System.out.println(result.get());
  }
}
