package com.hmall.common.aspect;

import com.hmall.common.annotation.Logger;
import com.hmall.common.domain.WebLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@Aspect
@Slf4j
@ConditionalOnClass(DispatcherServlet.class)
public class LoggerAspect {

    @Pointcut("execution(* com.hmall..controller.*.*(..)) && @annotation(com.hmall.common.annotation.Logger)")
    public void loggerPointCut() {}

    @Around("loggerPointCut()")
    public Object loggerAround(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = getRequest();
        WebLog webLog;
        Object result = null;
        try {
            log.info("================前置通知===============");
            long start = System.currentTimeMillis();
            //执行拦截的方法
            result = joinPoint.proceed();
            log.info("================返回通知===============");
            long l = System.currentTimeMillis() - start;
            long timeCost = TimeUnit.MILLISECONDS.toSeconds(l);
            Logger logger = getAnnotationLogger(joinPoint);
            webLog = WebLog.builder()
                    .description(logger.description())
                    .startTime(start)
                    .timeCost(timeCost)
                    .url(request.getRequestURL().toString())
                    .uri(request.getRequestURI())
                    .ipAddress(request.getRemoteAddr())
                    .params(getParams(joinPoint))
                    .result(result)
                    .build();
            log.info(webLog.toString());
        } catch (Throwable e) {
            log.info("================异常通知===============");
            log.error(e.getMessage(), e);
        } finally {
            log.info("================后置通知===============");
        }
        return result;
    }

    /**
     * 获取方法参数
     * @param joinPoint
     * @return
     */
    private Object getParams(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = getMethodSignature(joinPoint);
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], parameterValues[i]);
        }
        return params;
    }


    private Logger getAnnotationLogger(ProceedingJoinPoint joinPoint) {
        return getMethodSignature(joinPoint).getMethod().getAnnotation(Logger.class);
    }

    private static MethodSignature getMethodSignature(ProceedingJoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    /**
     * 获取http请求对象
     * @return
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? requestAttributes.getRequest() : null;
    }
}
