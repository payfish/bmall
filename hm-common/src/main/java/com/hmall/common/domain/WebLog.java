package com.hmall.common.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Controller层的日志封装
 * @author Fu1sh
 */
@Data
@Builder
@ToString
public class WebLog {

    //操作描述
    private String description;
    //开始时间
    private Long startTime;
    //耗时
    private long timeCost;
    //URL
    private String url;
    //URI
    private String uri;
    //IP
    private String ipAddress;
    //请求类型
//    private String method;
    //请求参数
    private Object params;
    //返回结果
    private Object result;

}
