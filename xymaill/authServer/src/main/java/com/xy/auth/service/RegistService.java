package com.xy.auth.service;

import com.xy.common.to.UserRegistVo;
import com.xy.common.utils.R;

public interface RegistService {

    public R regist(UserRegistVo userRegistVo);
    public R login(UserRegistVo userRegistVo);
}
