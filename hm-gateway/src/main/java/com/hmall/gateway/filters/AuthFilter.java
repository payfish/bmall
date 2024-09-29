package com.hmall.gateway.filters;

import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.util.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Thread thread = Thread.currentThread();
        //1.get request
        ServerHttpRequest request = exchange.getRequest();
        //1.1 check request path to see if it needs a login interception
        RequestPath path = request.getPath();
        if (pathIsExclude(path)) {
            return chain.filter(exchange);
        }
        //2.get token from header
        //3.verify token,check if user has login
        String token = null;
        List<String> headers =  request.getHeaders().get("authorization");
        if (headers != null && !headers.isEmpty()) {
            token = headers.get(0);
        }
        //4.do jwt verifying
        Long userId;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            //Unauthorized!
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete(); //return 401
        }
        //5.pass userinfo to next filter
        ServerWebExchange swe = exchange.mutate().request(builder ->
                builder.header("user-id", userId.toString())).build();
        return chain.filter(swe);
    }

    /**
     * 判断路径是否合法
     * @param path
     * @return
     */
    private boolean pathIsExclude(RequestPath path) {
        List<String> excludePaths = authProperties.getExcludePaths();
        for (String pathPatten : excludePaths) {
            if (antPathMatcher.match(pathPatten, String.valueOf(path))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 定义Filter的优先级，需要低于NettyRoutingFilter的优先级Integer.MAX_VALUE
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
