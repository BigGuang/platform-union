package com.waps.service.jd.db.dao;

import com.waps.service.jd.db.model.WapsJdUser;
import com.waps.service.jd.db.model.WapsJdUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WapsJdUserDao {
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