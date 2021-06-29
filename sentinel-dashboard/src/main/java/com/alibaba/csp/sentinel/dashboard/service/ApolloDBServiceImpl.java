package com.alibaba.csp.sentinel.dashboard.service;

import com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.ApolloConfig;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.ApolloConfigUtil;
import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceGrayDelReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenReleaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: Apollo service
 * @author: lizz
 * @date: 2021/6/29 14:55
 */
@Component
public class ApolloDBServiceImpl implements DBService {

    @Value("${sentinel.apollo.env}")
    public String SENTINEL_APOLLO_ENV;

    @Autowired
    private ApolloOpenApiClient apolloOpenApiClient;

    @Override
    public void createOrUpdateItem(String app, String namespace, String rules) {
        // Increase the configuration
        String flowDataId = ApolloConfigUtil.getClusterAssignDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(rules);
        openItemDTO.setComment("Program auto-join");
        openItemDTO.setDataChangeCreatedBy(ApolloConfig.SENTINEL_APOLLO_USER);
        apolloOpenApiClient.createOrUpdateItem(
                ApolloConfig.SENTINEL_APOLLO_APPID, SENTINEL_APOLLO_ENV,
                ApolloConfig.SENTINEL_APOLLO_CLUSTERNAME, namespace, openItemDTO);
        // apollo配置发布
        releaseParam(namespace);
    }

    private void releaseParam(String namespace) {
        // Release configuration
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("Modify or add configurations");
        namespaceReleaseDTO.setReleasedBy(ApolloConfig.SENTINEL_APOLLO_USER);
        namespaceReleaseDTO.setReleaseTitle("Modify or add configurations");
        apolloOpenApiClient.publishNamespace(ApolloConfig.SENTINEL_APOLLO_APPID, SENTINEL_APOLLO_ENV,
                ApolloConfig.SENTINEL_APOLLO_CLUSTERNAME, namespace,
                namespaceReleaseDTO);
    }
}
