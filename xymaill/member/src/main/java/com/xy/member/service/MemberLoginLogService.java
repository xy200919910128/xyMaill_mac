package com.xy.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.PageUtils;
import com.xy.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 14:00:33
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

