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

import com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将规则上传至apollo中
 */
@Component("apolloClusterAppAssignPublisher")
public class ApolloClusterAppAssignPublisher implements DynamicRulePublisher<List<ClusterAppAssignMap>> {
    /**
     * apollo动态环境配置，便于测试、线上环境切换
     */
    @Value("${sentinel.apollo.env}")
    public String SENTINEL_APOLLO_ENV;

    @Autowired
    private ApolloOpenApiClient apolloOpenApiClient;
    @Override
    public void publish(String app, List<ClusterAppAssignMap> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        // Increase the configuration
        String flowDataId = ApolloConfigUtil.getClusterAssignDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(JSON.toJSONString(rules));
        openItemDTO.setComment("Program auto-join");
        openItemDTO.setDataChangeCreatedBy(ApolloConfig.SENTINEL_APOLLO_USER);
        apolloOpenApiClient.createOrUpdateItem(
                ApolloConfig.SENTINEL_APOLLO_APPID, SENTINEL_APOLLO_ENV,
                ApolloConfig.SENTINEL_APOLLO_CLUSTERNAME, ApolloConfig.SENTINEL_APOLLO_NAMESPACE, openItemDTO);

        // Release configuration
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("Modify or add configurations");
        namespaceReleaseDTO.setReleasedBy(ApolloConfig.SENTINEL_APOLLO_USER);
        namespaceReleaseDTO.setReleaseTitle("Modify or add configurations");
        apolloOpenApiClient.publishNamespace(ApolloConfig.SENTINEL_APOLLO_APPID, SENTINEL_APOLLO_ENV,
                ApolloConfig.SENTINEL_APOLLO_CLUSTERNAME, ApolloConfig.SENTINEL_APOLLO_NAMESPACE,
                namespaceReleaseDTO);
    }
}
