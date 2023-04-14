package com.openAi.yunchat.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.openAi.yunchat.core.utils.WebUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * 用于自动填充时间\新增、修改人
 */
public class MybatisObjectHandler implements MetaObjectHandler {

    private static final String  CREATE_TIME_COLUMN = "createTime";
    private static final String UPDATE_TIME_COLUMN = "modifyTime";
    private static final String CREATOR_ID_COLUMN = "creatorId";
    private static final String CREATOR_NAME_COLUMN = "creatorName";
    private static final String MODIFIER_ID_COLUMN = "modifierId";
    private static final String MODIFIER_NAME_COLUMN = "modifierName";

    @Override
    public void insertFill(MetaObject metaObject) {
        if(metaObject.hasSetter(CREATE_TIME_COLUMN)){
            this.setFieldValByName(CREATE_TIME_COLUMN,LocalDateTime.now(),metaObject);
        }
        if(metaObject.hasSetter(UPDATE_TIME_COLUMN)){
            this.setFieldValByName(UPDATE_TIME_COLUMN,LocalDateTime.now(),metaObject);
        }
        if(metaObject.hasSetter(CREATOR_ID_COLUMN)){
            this.setFieldValByName(CREATOR_ID_COLUMN, WebUtils.getId(),metaObject);
        }
        if(metaObject.hasSetter(CREATOR_NAME_COLUMN)){
            this.setFieldValByName(CREATOR_NAME_COLUMN,WebUtils.getName(),metaObject);
        }
        if(metaObject.hasSetter(MODIFIER_ID_COLUMN)){
            this.setFieldValByName(MODIFIER_ID_COLUMN,WebUtils.getId(),metaObject);
        }
        if(metaObject.hasSetter(MODIFIER_NAME_COLUMN)){
            this.setFieldValByName(MODIFIER_NAME_COLUMN,WebUtils.getName(),metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasSetter(UPDATE_TIME_COLUMN)){
            this.setFieldValByName(UPDATE_TIME_COLUMN, LocalDateTime.now(),metaObject);
        }
        if(metaObject.hasSetter(MODIFIER_ID_COLUMN)){
            this.setFieldValByName(MODIFIER_ID_COLUMN,WebUtils.getId(),metaObject);
        }
        if(metaObject.hasSetter(MODIFIER_NAME_COLUMN)){
            this.setFieldValByName(MODIFIER_NAME_COLUMN,WebUtils.getName(),metaObject);
        }
    }
}
