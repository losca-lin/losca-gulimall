package com.losca.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.IdType.INPUT;

/**
 * spu信息介绍
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:50:59
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type = INPUT)
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
