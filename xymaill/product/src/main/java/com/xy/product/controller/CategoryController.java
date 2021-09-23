package com.xy.product.controller;

import com.xy.common.utils.PageUtils;
import com.xy.common.utils.R;
import com.xy.product.entity.CategoryEntity;
import com.xy.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品三级分类
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
@RestController
@RequestMapping("maillproduct/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("maillproduct:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 树形结构列表
     */
    @RequestMapping("/list/tree")
    public R listTree(){
        List<CategoryEntity> cateList = categoryService.queryTree();
        return R.ok().put("cateList", cateList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("maillproduct:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("maillproduct:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("maillproduct:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateDetail(category);
        return R.ok();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/ids")
    public R deleteIds(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete/id")
    public R deleteId(@RequestBody Long[] catId){
        System.out.println("删除的id："+catId[0]);

        //categoryService.removeById(catId);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R newDeleteId(@RequestBody Long[] catId){
        categoryService.removeByIds(Arrays.asList(catId));
        return R.ok();
    }



    /**
     * 逻辑删除
     */
    @RequestMapping("/logicDelete/id")
    public R logicDeleteId(@RequestBody Long[] catId){
        System.out.println("逻辑删除的id："+catId[0]);
        List<Long> idList = Arrays.stream(catId).filter((id)->{
            return id!=null&&id>0;
        }).collect(Collectors.toList());
        categoryService.logicDelete(idList);
        return R.ok();
    }

}
