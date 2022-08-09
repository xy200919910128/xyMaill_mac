package com.xy.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.to.UserRegistVo;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.R;
import com.xy.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 14:00:33
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R regist(UserRegistVo userRegistVo);
}

