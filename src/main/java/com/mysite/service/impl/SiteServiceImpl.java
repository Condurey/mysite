package com.mysite.service.impl;

import com.github.pagehelper.PageHelper;
import com.mysite.constant.ErrorConstant;
import com.mysite.constant.Types;
import com.mysite.constant.WebConst;
import com.mysite.dao.AttachDao;
import com.mysite.dao.CommentDao;
import com.mysite.dao.ContentDao;
import com.mysite.dao.MetaDao;
import com.mysite.exception.BusinessException;
import com.mysite.model.dto.ArchiveDto;
import com.mysite.model.dto.MetaDto;
import com.mysite.model.dto.StatisticsDto;
import com.mysite.model.po.Comment;
import com.mysite.model.po.Content;
import com.mysite.model.query.CommentQuery;
import com.mysite.model.query.ContentQuery;
import com.mysite.model.query.MetaQuery;
import com.mysite.service.SiteService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 站点服务
 * Created by Donghua.Chen on 2018/4/30.
 */
@Service("siteService")
public class SiteServiceImpl implements SiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Resource
    private CommentDao commentDao;

    @Resource
    private ContentDao contentDao;

    @Resource
    private MetaDao metaDao;

    @Resource
    private AttachDao attachDao;

    @Override
    @Cacheable(value = "siteCache", key = "'comments_' + #p0")
    public List<Comment> getComments(int limit) {
        LOGGER.debug("Enter recentComments method:limit={}", limit);
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        PageHelper.startPage(1, limit);
        List<Comment> rs = commentDao.getCommentsByCond(new CommentQuery());
        LOGGER.debug("Exit recentComments method");
        return rs;
    }

    @Override
    @Cacheable(value = "siteCache", key = "'newArticles_' + #p0")
    public List<Content> getNewArticles(int limit) {
        LOGGER.debug("Enter recentArticles method:limit={}", limit);
        if (limit < 0 || limit > 10)
            limit = 10;
        PageHelper.startPage(1, limit);
        List<Content> rs = contentDao.getArticlesByCond(new ContentQuery());
        LOGGER.debug("Exit recentArticles method");
        return rs;
    }

    @Override
    @Cacheable(value = "siteCache", key = "'comment_' + #p0")
    public Comment getComment(Integer coid) {
        LOGGER.debug("Enter recentComment method");
        if (null == coid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        Comment comment = commentDao.getCommentById(coid);
        LOGGER.debug("Exit recentComment method");
        return comment;
    }

    @Override
    @Cacheable(value = "siteCache", key = "'statistics_'")
    public StatisticsDto getStatistics() {
        LOGGER.debug("Enter recentStatistics method");
        //文章总数
        Long artices = contentDao.getArticleCount();

        Long comments = commentDao.getCommentsCount();

        Long links = metaDao.getMetasCountByType(Types.LINK.getType());

        Long atts = attachDao.getAttsCount();

        StatisticsDto rs = new StatisticsDto();
        rs.setArticles(artices);
        rs.setAttachs(atts);
        rs.setComments(comments);
        rs.setLinks(links);

        LOGGER.debug("Exit recentStatistics method");
        return rs;
    }

    @Override
    @Cacheable(value = "siteCache", key = "'archivesSimple_' + #p0")
    public List<ArchiveDto> getArchivesSimple(ContentQuery contentQuery) {
        LOGGER.debug("Enter getArchives method");
        List<ArchiveDto> archives = contentDao.getArchive(contentQuery);
        LOGGER.debug("Exit getArchives method");
        return archives;
    }

    @Override
    @Cacheable(value = "siteCache", key = "'archives_' + #p0")
    public List<ArchiveDto> getArchives(ContentQuery contentQuery) {
        LOGGER.debug("Enter getArchives method");
        List<ArchiveDto> archives = contentDao.getArchive(contentQuery);
        parseArchives(archives, contentQuery);
        LOGGER.debug("Exit getArchives method");
        return archives;
    }


    private void parseArchives(List<ArchiveDto> archives, ContentQuery contentQuery) {
        if (null != archives) {
            archives.forEach(archive -> {
                String date = archive.getDate();
//                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
//                int start = DateKit.getUnixTimeByDate(sd);
//                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;
                ContentQuery cond = new ContentQuery();
//                cond.setStartTime(start);
//                cond.setEndTime(end);
                cond.setType(contentQuery.getType());
                List<Content> contentss = contentDao.getArticlesByCond(cond);
                archive.setArticles(contentss);
            });
        }
    }

    @Override
    @Cacheable(value = "siteCache", key = "'metas_' + #p0")
    public List<MetaDto> getMetas(String type, String order, int limit) {
        LOGGER.debug("Enter metas method:type={},order={},limit={}", type, order, limit);
        List<MetaDto> metas = null;
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(order)) {
                order = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            MetaQuery metaQuery = new MetaQuery();
            metaQuery.setType(type);
            metaQuery.setOrder(order);
            metaQuery.setLimit(limit);
            metas = metaDao.selectFromSql(metaQuery);
        }
        LOGGER.debug("Exit metas method");
        return metas;
    }
}
