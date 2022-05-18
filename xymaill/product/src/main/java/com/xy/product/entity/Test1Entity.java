package com.xy.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Test1Entity信息
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:37
 */
@Data
@TableName("test1")
public class Test1Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type= IdType.INPUT)
	private Long id;
	/**
	 * 商品名称
	 */
	private String name;

	private String generate;
}
