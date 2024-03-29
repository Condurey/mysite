package com.mysite.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysite.constant.ErrorConstant;
import com.mysite.dao.LogDao;
import com.mysite.exception.BusinessException;
import com.mysite.model.po.Log;
import com.mysite.service.LogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 请求日志
 * Created by Donghua.Chen on 2018/4/29.
 */
@Service("logService")
public class LogServiceImpl implements LogService {

    @Resource
    private LogDao logDao;

    @Override
    public void addLog(String action, String data, String ip, Integer authorId) {
        Log log = new Log();
        log.setAuthorId(authorId);
        log.setIp(ip);
        log.setData(data);
        log.setAction(action);
        logDao.addLog(log);
    }

    @Override
    public void deleteLogById(Integer id) {
        if (null == id)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        deleteLogById(id);
    }

    @Override
    public PageInfo<Log> getLogs(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Log> logs = logDao.getLogs();
        PageInfo<Log> pageInfo = new PageInfo<>(logs);
        return pageInfo;
    }
}
