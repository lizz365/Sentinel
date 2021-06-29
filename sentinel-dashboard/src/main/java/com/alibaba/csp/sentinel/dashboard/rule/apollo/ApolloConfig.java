/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hantianwei@gmail.com
 * @since 1.5.0
 */
@Configuration
public class ApolloConfig {
    /**
     * apollo系统地址
     */
    @Value("${sentinel.apollo.url}")
    private String SENTINEL_APOLLO_URL;
    /**
     * 通过api操作apollo的token，必须与操作user匹配，在apollo管理页面中开通
     */
    @Value("${sentinel.apollo.token}")
    private String SENTINEL_APOLLO_TOKEN;
    /**
     * apollo中存储数据的项目id
     */
    public static final String SENTINEL_APOLLO_APPID = "fw-AliSentinel";
    /**
     * apollo中存储数据的namespace，需要新建公共namespace
     */
    public static final String SENTINEL_APOLLO_NAMESPACE = "jiagou.sentinel-application";
    public static final String SENTINEL_CLUSTER_APOLLO_NAMESPACE = "jiagou.sentinel-cluster";
    /**
     * 环境列表没有特别新建集群，就是default。
     */
    public static final String SENTINEL_APOLLO_CLUSTERNAME = "default";
    /**
     * 操作数据apollo的用户名称，在apollo中需要有操作权限
     */
    public static final String SENTINEL_APOLLO_USER = "lizhenzhong";

    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }
    /**
     *
     * @return
     */
    @Bean
    public ApolloOpenApiClient apolloOpenApiClient() {
        ApolloOpenApiClient client = ApolloOpenApiClient.newBuilder()
            .withPortalUrl(SENTINEL_APOLLO_URL)
            .withToken(SENTINEL_APOLLO_TOKEN)
            .build();
        return client;

    }
}
