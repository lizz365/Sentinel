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
import com.alibaba.csp.sentinel.dashboard.service.DBService;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将规则上传至apollo中
 */
@Component("apolloClusterAppAssignPublisher")
public class ApolloClusterAppAssignPublisher implements DynamicRulePublisher<List<ClusterAppAssignMap>> {
    @Autowired
    private DBService apolloDBService;

    @Override
    public void publish(String app, List<ClusterAppAssignMap> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        // Increase the configuration
        String flowDataId = ApolloConfigUtil.getClusterAssignDataId(app);
        apolloDBService.createOrUpdateItem(app, ApolloConfig.SENTINEL_APOLLO_NAMESPACE, flowDataId, JSON.toJSONString(rules));
    }
}
