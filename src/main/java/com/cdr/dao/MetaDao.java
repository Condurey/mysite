package com.cdr.dao;

import com.cdr.model.Meta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 项目dao
 * Created by Donghua.Chen on 2018/4/29.
 */
@Mapper
public interface MetaDao {

    /**
     * 添加项目
     * @param meta
     * @return
     */
    int addMeta(Meta meta);

    /**
     * 删除项目
     * @param mid
     * @return
     */
    int deleteMetaById(@Param("mid") Integer mid);

    /**
     * 更新项目
     * @param meta
     * @return
     */
    int updateMeta(Meta meta);

    /**
     * 根据编号获取项目
     * @param mid
     * @return
     */
    Meta getMetaById(@Param("mid") Integer mid);


    /**
     * 根据条件查询
     * @param meta
     * @return
     */
    List<Meta> getMetasByCond(Meta meta);

    /**
     * 根据类型获取meta数量
     * @param type
     * @return
     */
    Long getMetasCountByType(@Param("type") String type);

    /**
     * 根据sql查询
     * @param paraMap
     * @return
     */
    List<Meta> selectFromSql(Map<String, Object> paraMap);

}
