package jnpf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jnpf.base.UserInfo;


import jnpf.base.service.SuperServiceImpl;

import jnpf.constant.CodeConst;
import jnpf.constant.PermissionConst;

import jnpf.entity.SystemEntity;
import jnpf.mapper.SystemMapper;
import jnpf.service.SystemService;
import jnpf.util.RandomUtil;
import jnpf.util.StringUtil;
import jnpf.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 系统
 *
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class SystemServiceImpl extends SuperServiceImpl<SystemMapper, SystemEntity> implements SystemService {




    @Override
    public List<SystemEntity> getList() {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SystemEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(SystemEntity::getSortCode)
                .orderByDesc(SystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SystemEntity> getList(String keyword, Boolean filterEnableMark, boolean verifyAuth, Boolean filterMain, boolean isList, List<String> moduleAuthorize) {
        UserInfo user = UserProvider.getUser();

        boolean flag = false;
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(keyword)) {
            flag = true;
            queryWrapper.lambda().and(t ->
                    t.like(SystemEntity::getFullName, keyword).or().like(SystemEntity::getEnCode, keyword)
                            .or().like(SystemEntity::getDescription, keyword)
            );
        }
        if (filterEnableMark == null) {
            queryWrapper.lambda().eq(SystemEntity::getEnabledMark, 0);
        } else if (filterEnableMark) {
            queryWrapper.lambda().eq(SystemEntity::getEnabledMark, 1);
        }

        // 过滤掉系统应用
        if (filterMain != null && filterMain) {
            queryWrapper.lambda().ne(SystemEntity::getIsMain, 1);
        }
        //判断权限列表
        if (!user.getIsAdministrator() && verifyAuth) {
            if (user.getIsDevRole()) {
                queryWrapper.lambda().and(t -> t
                        .eq(SystemEntity::getUserId, user.getUserId()).or()
                        .like(SystemEntity::getAuthorizeId, user.getUserId()).or()
                        .eq(SystemEntity::getAuthorizeId, PermissionConst.ALL_DEV_USER));
            }
        }

        //过滤租户分配黑名单
        if (moduleAuthorize.size() > 0) {
            queryWrapper.lambda().notIn(SystemEntity::getId, moduleAuthorize);
        }
        if (flag) {
            queryWrapper.lambda().orderByDesc(SystemEntity::getLastModifyTime);
        } else {
            queryWrapper.lambda().orderByAsc(SystemEntity::getSortCode).orderByDesc(SystemEntity::getCreatorTime);
        }

        return this.list(queryWrapper);
    }

    @Override
    public List<SystemEntity> getListByIdsKey(List<String> ids, String keyword) {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SystemEntity::getId, ids);
        boolean flag = false;
        if (StringUtil.isNotEmpty(keyword)) {
            flag = true;
            queryWrapper.lambda().and(t ->
                    t.like(SystemEntity::getFullName, keyword).or().like(SystemEntity::getEnCode, keyword)
                            .or().like(SystemEntity::getDescription, keyword)
            );
        }
        if (flag) {
            queryWrapper.lambda().orderByDesc(SystemEntity::getLastModifyTime);
        } else {
            queryWrapper.lambda().orderByAsc(SystemEntity::getSortCode).orderByDesc(SystemEntity::getCreatorTime);
        }
        return this.list(queryWrapper);
    }

    @Override
    public SystemEntity getInfo(String id) {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SystemEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public Boolean isExistFullName(String id, String fullName) {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SystemEntity::getFullName, fullName);
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(SystemEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean isExistEnCode(String id, String enCode) {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SystemEntity::getEnCode, enCode);
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(SystemEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }





    @Override
    public List<SystemEntity> getListByIds(List<String> list, List<String> moduleAuthorize) {
        List<SystemEntity> systemList = new ArrayList<>(16);
        if (list.size() > 0) {
            QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
            if (moduleAuthorize != null && moduleAuthorize.size() > 0) {
                queryWrapper.lambda().notIn(SystemEntity::getId, moduleAuthorize);
            }
            queryWrapper.lambda().in(SystemEntity::getId, list);
            queryWrapper.lambda().eq(SystemEntity::getEnabledMark, 1);
            queryWrapper.lambda().orderByAsc(SystemEntity::getSortCode).orderByDesc(SystemEntity::getCreatorTime);
            return this.list(queryWrapper);
        }
        return systemList;
    }

    @Override
    public SystemEntity getInfoByEnCode(String enCode) {
        if (StringUtil.isEmpty(enCode)) {
            return null;
        }
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SystemEntity::getEnCode, enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SystemEntity> findSystemAdmin(List<String> moduleAuthorize) {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        if (moduleAuthorize != null && moduleAuthorize.size() > 0) {
            queryWrapper.lambda().notIn(SystemEntity::getId, moduleAuthorize);
        }
        queryWrapper.lambda().orderByAsc(SystemEntity::getSortCode).orderByDesc(SystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SystemEntity> findSystemAdmin(int mark, String mainSystemCode) {
        QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
        if (mark == 1) {
            queryWrapper.lambda().eq(SystemEntity::getEnabledMark, mark)
                    .ne(SystemEntity::getEnCode, mainSystemCode);
        }
        queryWrapper.lambda().orderByAsc(SystemEntity::getSortCode).orderByDesc(SystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }



    @Override
    public List<SystemEntity> getAuthListByUser() {
        UserInfo user = UserProvider.getUser();
        //开发人员才有编辑权限
        if (user.getIsDevRole() || user.getIsAdministrator()) {
            QueryWrapper<SystemEntity> queryWrapper = new QueryWrapper<>();
            //判断权限列表
            if (!user.getIsAdministrator()) {
                queryWrapper.lambda().eq(SystemEntity::getUserId, user.getUserId()).or();
                queryWrapper.lambda().like(SystemEntity::getAuthorizeId, user.getUserId()).or();
                queryWrapper.lambda().eq(SystemEntity::getAuthorizeId, PermissionConst.ALL_DEV_USER);
            }
            return list(queryWrapper);
        }
        return Collections.EMPTY_LIST;
    }

}
