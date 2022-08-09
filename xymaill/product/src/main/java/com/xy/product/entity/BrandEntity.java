package com.xy.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xy.common.validate.AddGroups;
import com.xy.common.validate.ListValue;
import com.xy.common.validate.UpdateGroups;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 * 校验分组 如果在controller开启  那么这个校验只会在当前分组生效 其他分组都不生效 如果 不在controller指定 那么只有没定义分组的校验生效 定义的则不生效 在controller中 定义使用哪个分组要加{}
 * 在common包中定义两个分组接口即可
 *
 */
@Data
@TableName("pms_brand")
@NoArgsConstructor
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "品牌id在新增的时候必须为空",groups = {AddGroups.class})
	@NotNull(message = "品牌id在修改的时候不能为空",groups = {UpdateGroups.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名称不能为空")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty
	@URL(message = "logo必须是合法的url",groups = {AddGroups.class,UpdateGroups.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(vals = {0,1},groups = {AddGroups.class}) //这个使用的自定义校验注解：（ListValue）(需要三步)
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "首字母不能为空",groups = {AddGroups.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "首字母必须在a-z或A-Z之间")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空",groups = {AddGroups.class})
	@Min(value = 0,message = "排序必须大于0")
	private Integer sort;

}
