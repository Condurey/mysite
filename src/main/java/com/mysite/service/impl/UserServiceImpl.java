package com.mysite.service.impl;

import com.mysite.constant.ErrorConstant;
import com.mysite.dao.UserDao;
import com.mysite.exception.BusinessException;
import com.mysite.model.po.User;
import com.mysite.service.UserService;
import com.mysite.utils.EncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Donghua.Chen on 2018/4/20.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;//这里会报错，但是并不会影响


    @Transactional
    @Override
    public int updateUserInfo(User user) {
        if (null == user.getUid())
            throw BusinessException.withErrorCode("用户编号不可能为空");
        return userDao.updateUserInfo(user);
    }

    @Override
    public User getUserInfoById(Integer uId) {
        return userDao.getUserInfoById(uId);
    }

    @Override
    public User login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password))
            throw BusinessException.withErrorCode(ErrorConstant.Auth.USERNAME_PASSWORD_IS_EMPTY);

        String pwd = EncryptUtils.MD5encode(username + password);
        User user = userDao.getUserInfoByCond(username, pwd);
        if (null == user)
            throw BusinessException.withErrorCode(ErrorConstant.Auth.USERNAME_PASSWORD_ERROR);

        return user;
    }


}
