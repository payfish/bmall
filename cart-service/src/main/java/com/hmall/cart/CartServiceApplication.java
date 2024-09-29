package com.hmall.cart;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.hmall.cart.mapper")
@SpringBootApplication
@EnableDubbo
@Slf4j
//@EnableFeignClients(basePackages = "com.hmall.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class CartServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
//        DubboAdapterGlobalConfig.setConsumerFallback((invoker, invocation, e) -> {
//            try {
//                Object[] arguments = invocation.getArguments();
//                String serviceName = invocation.getServiceName();
//                String methodName = invocation.getMethodName();
//                log.error("降级了，参数：{}, 服务名：{}, 方法：{}", arguments, serviceName, methodName);
//            } catch (ClassCastException cce) {
//                log.error("降级时发生类型转换异常：{}", cce.getMessage(), cce);
//            }
//            return (Result) AsyncRpcResult.newDefaultAsyncResult(invocation);
//        });
    }
}