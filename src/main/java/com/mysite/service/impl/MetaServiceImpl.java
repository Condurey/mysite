package com.mysite.service.impl;

import com.mysite.constant.ErrorConstant;
import com.mysite.constant.Types;
import com.mysite.constant.WebConst;
import com.mysite.dao.MetaDao;
import com.mysite.exception.BusinessException;
import com.mysite.model.dto.MetaDto;
import com.mysite.model.dto.cond.MetaCond;
import com.mysite.model.entity.Content;
import com.mysite.model.entity.Meta;
import com.mysite.model.entity.Relationship;
import com.mysite.service.ContentService;
import com.mysite.service.MetaService;
import com.mysite.service.RelationshipService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Donghua.Chen on 2018/4/29.
 */
@Service
public class MetaServiceImpl implements MetaService {

    @Autowired
    private MetaDao metaDao;

    @Autowired
    private RelationshipService relationshipService;


    @Autowired
    private ContentService contentService;

    @Override
    @Transactional
    @CacheEvict(value = {"metaCaches", "metaCache"}, allEntries = true, beforeInvocation = true)
    public void addMeta(Meta meta) {
        if (null == meta)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        metaDao.addMeta(meta);

    }

    @Override
    @CacheEvict(value = {"metaCaches", "metaCache"}, allEntries = true, beforeInvocation = true)
    public void saveMeta(String type, String name, Integer mid) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            MetaCond metaCond = new MetaCond();
            metaCond.setName(name);
            metaCond.setType(type);
            List<Meta> metas = metaDao.getMetasByCond(metaCond);
            if (null == metas || metas.size() == 0) {
                Meta metaDomain = new Meta();
                metaDomain.setName(name);
                if (null != mid) {
                    Meta meta = metaDao.getMetaById(mid);
                    if (null != meta)
                        metaDomain.setMid(mid);

                    metaDao.updateMeta(metaDomain);
                    //更新原有的文章分类
                    contentService.updateCategory(meta.getName(), name);
                } else {
                    metaDomain.setType(type);
                    metaDao.addMeta(metaDomain);
                }
            } else {
                throw BusinessException.withErrorCode(ErrorConstant.Meta.META_IS_EXIST);

            }

        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"metaCaches", "metaCache"}, allEntries = true, beforeInvocation = true)
    public void addMetas(Integer cid, String names, String type) {
        if (null == cid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);

        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    @Override
    @CacheEvict(value = {"metaCaches", "metaCache"}, allEntries = true, beforeInvocation = true)
    public void saveOrUpdate(Integer cid, String name, String type) {
        MetaCond metaCond = new MetaCond();
        metaCond.setName(name);
        metaCond.setType(type);
        List<Meta> metas = this.getMetas(metaCond);

        int mid;
        Meta metaDomain;
        if (metas.size() == 1) {
            Meta meta = metas.get(0);
            mid = meta.getMid();
        } else if (metas.size() > 1) {
            throw BusinessException.withErrorCode(ErrorConstant.Meta.NOT_ONE_RESULT);
        } else {
            metaDomain = new Meta();
            metaDomain.setSlug(name);
            metaDomain.setName(name);
            metaDomain.setType(type);
            this.addMeta(metaDomain);
            mid = metaDomain.getMid();
        }
        if (mid != 0) {
            Long count = relationshipService.getCountById(cid, mid);
            if (count == 0) {
                Relationship relationship = new Relationship();
                relationship.setCid(cid);
                relationship.setMid(mid);
                relationshipService.addRelationship(relationship);
            }

        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"metaCaches", "metaCache"}, allEntries = true, beforeInvocation = true)
    public void deleteMetaById(Integer mid) {
        if (null == mid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);

        Meta meta = metaDao.getMetaById(mid);
        if (null != meta) {
            String type = meta.getType();
            String name = meta.getName();
            metaDao.deleteMetaById(mid);
            //需要把相关的数据删除
            List<Relationship> relationships = relationshipService.getRelationshipByMid(mid);
            if (null != relationships && relationships.size() > 0) {
                for (Relationship relationship : relationships) {
                    Content article = contentService.getAtricleById(relationship.getCid());
                    if (null != article) {
                        Content temp = new Content();
                        temp.setCid(relationship.getCid());
                        if (type.equals(Types.CATEGORY.getType())) {
                            temp.setCategories(reMeta(name, article.getCategories()));
                        }
                        if (type.equals(Types.TAG.getType())) {
                            temp.setTags(reMeta(name, article.getTags()));
                        }
                        //将删除的资源去除
                        contentService.updateArticleById(temp);
                    }
                }
                relationshipService.deleteRelationshipByMid(mid);
            }
        }


    }

    @Override
    @Transactional
    @CacheEvict(value = {"metaCaches", "metaCache"}, allEntries = true, beforeInvocation = true)
    public void updateMeta(Meta meta) {
        if (null == meta || null == meta.getMid())
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        metaDao.updateMeta(meta);

    }

    @Override
    @Cacheable(value = "metaCache", key = "'metaById_' + #p0")
    public Meta getMetaById(Integer mid) {
        if (null == mid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        return metaDao.getMetaById(mid);
    }

    @Override
    @Cacheable(value = "metaCaches", key = "'metas_' + #p0")
    public List<Meta> getMetas(MetaCond metaCond) {
        return metaDao.getMetasByCond(metaCond);
    }


    @Override
    @Cacheable(value = "metaCaches", key = "'metaList_' + #p0")
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderby);
            paraMap.put("limit", limit);
            return metaDao.selectFromSql(paraMap);
        }
        return null;
    }

    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }
}
