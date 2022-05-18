package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.product.entity.Test1Entity;

import java.util.List;

/**
 * spu信息
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:37
 */
public interface Test1Service extends IService<Test1Entity> {

    public List<Test1Entity> getCanSearch(List<Long> ids);

}

