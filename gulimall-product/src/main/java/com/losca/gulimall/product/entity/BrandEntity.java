package com.losca.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.losca.common.valid.AddGroup;
import com.losca.common.valid.ListValue;
import com.losca.common.valid.UpdateGroup;
import com.losca.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:50:59
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改品牌Id不能为空",groups = {UpdateGroup.class, UpdateStatusGroup.class})
	@Null(message = "新增品牌时Id为空",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo不能为空",groups = {AddGroup.class})
	@URL(message = "logo必须是正确的url地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "介绍不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "状态不能为空",groups = {AddGroup.class, UpdateStatusGroup.class})
	@ListValue(vals={0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "检索首字母不能为空",groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母只能为a-z或A-Z",groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空",groups = {AddGroup.class})
	@Min(value = 0,message = "排序最小为0",groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
