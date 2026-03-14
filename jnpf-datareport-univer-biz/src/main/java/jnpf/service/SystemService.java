package jnpf.service;


import jnpf.base.service.SuperService;
import jnpf.entity.SystemEntity;

import java.util.List;

/**
 * 系统
 *
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2019年9月27日 上午9:18
 */
public interface SystemService extends SuperService<SystemEntity> {

    /**
     * 获取列表
     *
     * @return
     */
    List<SystemEntity> getList();

    /**
     * 获取系统列表
     *
     * @param keyword
     * @param filterMain
     * @param isList
     * @param moduleAuthorize
     * @return
     */
    List<SystemEntity> getList(String keyword, Boolean filterEnableMark, boolean verifyAuth, Boolean filterMain, boolean isList, List<String> moduleAuthorize);

    /**
     * 获取列表
     *
     * @return
     */
    List<SystemEntity> getListByIdsKey(List<String> ids, String keyword);

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    SystemEntity getInfo(String id);

    /**
     * 判断系统名称是否重复
     *
     * @param id
     * @param fullName
     * @return
     */
    Boolean isExistFullName(String id, String fullName);

    /**
     * 判断系统编码是否重复
     *
     * @param id
     * @param enCode
     * @return
     */
    Boolean isExistEnCode(String id, String enCode);





    /**
     * 通过id获取系统列表
     *
     * @param list
     * @param moduleAuthorize
     * @return
     */
    List<SystemEntity> getListByIds(List<String> list, List<String> moduleAuthorize);

    /**
     * 通过编码获取系统信息
     *
     * @param enCode
     * @return
     */
    SystemEntity getInfoByEnCode(String enCode);

    /**
     * 获取
     *
     * @param moduleAuthorize
     * @return
     */
    List<SystemEntity> findSystemAdmin(List<String> moduleAuthorize);

    /**
     * 获取
     *
     * @param mark
     * @param mainSystemCode
     * @return
     */
    List<SystemEntity> findSystemAdmin(int mark, String mainSystemCode);


    /**
     * 获取当前用户有编辑权限的应用
     *
     * @return
     */
    List<SystemEntity> getAuthListByUser();
}
