package com.alibaba.csp.sentinel.dashboard.service;



public interface DBService {
    /**
     * 创建和更新值
     */
    void createOrUpdateItem(String app, String namespace, String flowDataId, String rules);
}
