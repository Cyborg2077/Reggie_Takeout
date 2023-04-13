package com.blog.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充(insert)...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //添加设置添加人id
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充(update)...");
        log.info(metaObject.toString());
        long id = Thread.currentThread().getId();
        log.info("updateFill的线程id为：{}", id);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //添加设置修改人id
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
    }
}

