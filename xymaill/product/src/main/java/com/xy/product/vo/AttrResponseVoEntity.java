package com.xy.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品属性
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
@Data
public class AttrResponseVoEntity extends AttrVoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String catelogName;
	private String groupName;
	private Long[] catelogPath;

}
