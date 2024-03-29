package com.mysite.service;

import com.github.pagehelper.PageInfo;
import com.mysite.model.po.Log;

/**
 * 用户请求日志
 * Created by Donghua.Chen on 2018/4/29.
 */
public interface LogService {

    /**
     * 添加
     *
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    void addLog(String action, String data, String ip, Integer authorId);

    /**
     * 删除日志
     *
     * @param id
     * @return
     */
    void deleteLogById(Integer id);

    /**
     * 获取日志
     *
     * @return
     */
    PageInfo<Log> getLogs(int pageNum, int pageSize);
}
