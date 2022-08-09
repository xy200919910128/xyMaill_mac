package com.xy.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.to.UserRegistVo;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.common.utils.R;
import com.xy.member.dao.MemberDao;
import com.xy.member.entity.MemberEntity;
import com.xy.member.entity.MemberLevelEntity;
import com.xy.member.service.MemberLevelService;
import com.xy.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R regist(UserRegistVo userRegistVo) {
        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity me = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_Status", 1));
        memberEntity.setCreateTime(new Date());
        memberEntity.setLevelId(me.getId());
        memberEntity.setUsername(userRegistVo.getUsername());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        memberEntity.setUsername(userRegistVo.getUsername());
        memberEntity.setPassword(bCryptPasswordEncoder.encode(userRegistVo.getPassword()));
        memberEntity.setMobile(userRegistVo.getPhone());
        try{
            this.save(memberEntity);
        }catch(Exception e){
            e.printStackTrace();
        }
        return R.ok();
    }

}