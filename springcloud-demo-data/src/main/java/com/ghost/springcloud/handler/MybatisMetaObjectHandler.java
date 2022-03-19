package com.ghost.springcloud.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/03/19 13:05
 */
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        strictFillStrategy(metaObject, "createTime", LocalDateTime::now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictFillStrategy(metaObject, "updateTime", LocalDateTime::now);
    }

}
