package cn.itz.cloud.controller;

import cn.itz.cloud.config.HelloCommand;
import cn.itz.cloud.service.HelloService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @PackageName: cn.itz.cloud.controller
 * @ClassName: HelloController
 * @Author: codeZhang
 * @DateTime: 2021/1/4 15:45
 * @Version 1.0
 */
@RestController
public class HelloController {

  @Autowired
  HelloService helloService;

  @Autowired
  RestTemplate restTemplate;

  @GetMapping("/hello")
  public String hello(){
    return helloService.hello();
  }

  /**
   * 一个实例只能执行一次
   * 可以直接执行，也可以先入队，后执行
   */
  @GetMapping("/hello2")
  public void hello2(){
    HelloCommand helloCommand = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("zhang")),restTemplate);
    String execute = helloCommand.execute(); //直接执行
    System.out.println(execute);
    HelloCommand helloCommand1 = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("zhang")), restTemplate);
    try {
      Future<String> queue = helloCommand1.queue();
      String s = queue.get();
      System.out.println(s);  //先入队，后执行
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/hello3")
  public void hello3(){
    Future<String> hello2 = helloService.hello2();
    String s = null;
    try {
      s = hello2.get();
      System.out.println(s);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/hello4")
  public void hello4(){
    HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
    String zhang = helloService.hello3("zhang");
    zhang = helloService.hello3("zhang");
    ctx.close();
  }
}