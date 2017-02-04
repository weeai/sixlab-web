/*
 * Copyright (c) 2016 Sixlab. All rights reserved.
 *
 * Under the GPLv3(AKA GNU GENERAL PUBLIC LICENSE Version 3).
 * see http://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * For more information, please see
 * http://sixlab.cn/
 *
 * @author 六楼的雨/Patrick Root
 * @since 1.0.0(2016/4/2)
 */
package cn.sixlab.web.server.module.tool;

import cn.sixlab.web.server.beans.ToolsShow;
import cn.sixlab.web.server.service.HisService;
import cn.sixlab.web.server.util.JsonMap;
import cn.sixlab.web.server.util.Meta;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author 六楼的雨/Patrick Root
 * @since 1.0.0(2016/4/2)
 */
@IocBean
@At(value = "/tool/show")
public class ShowModule {
    private static Logger logger = LoggerFactory.getLogger(ShowModule.class);
    
    @Inject
    protected Dao dao;
    
    @Inject
    private HisService hisService;
    
    @At(value = {"/", "/index"})
    public String index() {
        
        return "tool/show/index";
    }
    
    @At("/search")
    @Ok("json:compact")
    public JsonMap search(String keyword) {
        JsonMap json = new JsonMap();
    
        List<ToolsShow> showList;
        if (StringUtils.isEmpty(keyword)) {
            Condition cnd = Cnd.where("viewStatus", "=", Meta.SHOW_V_STATUS_ING);
            showList = dao.query(ToolsShow.class,cnd);
        } else {
            if (StringUtils.isNotEmpty(keyword)) {
                keyword = "%" + keyword + "%";
            }
    
            Condition cnd = Cnd.where("show_name", "like", keyword)
                    .or("tv", "like", keyword)
                    .or("remark", "like", keyword);
            showList = dao.query(ToolsShow.class, cnd);
        }
    
        int num = 0;
        if (!CollectionUtils.isEmpty(showList)) {
            num = showList.size();
            json.put("shows", showList);
        }
        json.put("num", num);
        
        return json;
    }
    
    @At("/add")
    @Ok("json:compact")
    public JsonMap add(@Param("..")ToolsShow toolsShow) {
        JsonMap json = new JsonMap();
    
        toolsShow.setViewStatus(Meta.SHOW_V_STATUS_ING);
        toolsShow.setShowStatus(Meta.SHOW_STATUS_ING);
        toolsShow.setUpdateDate(new Date());
        dao.insert(toolsShow);
        hisService.beginShow(toolsShow);
        
        return json;
    }
    
    @At("/season/add")
    @Ok("json:compact")
    public JsonMap seasonAdd(int id) {
        JsonMap json = new JsonMap();
    
        ToolsShow toolsShow = dao.fetch(ToolsShow.class, id);
        toolsShow.setShowStatus(Meta.SHOW_STATUS_ING);
        toolsShow.setShowEpisode(1);
        toolsShow.setShowSeason(toolsShow.getShowSeason() + 1);
        toolsShow.setUpdateDate(new Date());
        dao.update(toolsShow);
        hisService.addSeason(toolsShow);
        
        return json;
    }
    
    @At("/episode/add")
    @Ok("json:compact")
    public JsonMap episodeAdd(int id) {
        JsonMap json = new JsonMap();
    
        ToolsShow toolsShow = dao.fetch(ToolsShow.class, id);
        toolsShow.setShowStatus(Meta.SHOW_STATUS_ING);
        toolsShow.setShowEpisode(toolsShow.getShowEpisode() + 1);
        toolsShow.setUpdateDate(new Date());
        dao.update(toolsShow);
        hisService.addEpisode(toolsShow);
        
        return json;
    }
    
    @At("/end")
    @Ok("json:compact")
    public JsonMap end(int id) {
        JsonMap json = new JsonMap();
    
        ToolsShow toolsShow = dao.fetch(ToolsShow.class, id);
        toolsShow.setShowStatus(Meta.SHOW_STATUS_END);
        dao.update(toolsShow);
        
        return json;
    }
    
    @At("/finish")
    @Ok("json:compact")
    public JsonMap finish(int id) {
        JsonMap json = new JsonMap();
    
        ToolsShow toolsShow = dao.fetch(ToolsShow.class, id);
        toolsShow.setViewStatus(Meta.SHOW_V_STATUS_FINISH);
        dao.update(toolsShow);
        
        return json;
    }
}