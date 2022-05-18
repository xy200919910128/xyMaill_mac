package com.xy.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.constant.ProductConstant;
import com.xy.common.to.SkuHasStckVo;
import com.xy.common.to.SkuReductionTo;
import com.xy.common.to.SpuBoundTo;
import com.xy.common.to.es.Attrs;
import com.xy.common.to.es.SkuEsModel;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.common.utils.R;
import com.xy.product.dao.SpuInfoDao;
import com.xy.product.dao.Test1Dao;
import com.xy.product.entity.*;
import com.xy.product.feign.CouponFeignService;
import com.xy.product.feign.SearchFeignService;
import com.xy.product.feign.WareFeignService;
import com.xy.product.service.*;
import com.xy.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("test1Service")
public class Test1ServiceImpl extends ServiceImpl<Test1Dao, Test1Entity> implements Test1Service {

    @Autowired
    private Test1Dao test1Dao;

    @Override
    public List<Test1Entity> getCanSearch(List<Long> ids) {
       return test1Dao.getCanSearch1(ids);
    }
}