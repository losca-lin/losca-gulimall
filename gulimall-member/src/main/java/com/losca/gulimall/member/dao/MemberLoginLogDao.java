package com.losca.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.losca.gulimall.member.entity.MemberLoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 16:13:02
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
