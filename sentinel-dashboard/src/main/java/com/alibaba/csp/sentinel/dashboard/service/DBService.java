package com.alibaba.csp.sentinel.dashboard.service;


import java.util.List;

public interface DBService {
    /**
     *  创建和更新值
     */
    void createOrUpdateItem(String app, String namespace, String rules);
}
