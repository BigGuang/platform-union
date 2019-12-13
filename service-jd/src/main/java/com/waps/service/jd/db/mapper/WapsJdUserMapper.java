package com.waps.service.jd.db.mapper;

import com.waps.service.jd.db.domain.WapsJdUser;
import com.waps.service.jd.db.example.WapsJdUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WapsJdUserMapper {
    long countByExample(WapsJdUserExample example);

    int deleteByExample(WapsJdUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(WapsJdUser record);

    int insertSelective(WapsJdUser record);

    List<WapsJdUser> selectByExample(WapsJdUserExample example);

    WapsJdUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") WapsJdUser record, @Param("example") WapsJdUserExample example);

    int updateByExample(@Param("record") WapsJdUser record, @Param("example") WapsJdUserExample example);

    int updateByPrimaryKeySelective(WapsJdUser record);

    int updateByPrimaryKey(WapsJdUser record);
}