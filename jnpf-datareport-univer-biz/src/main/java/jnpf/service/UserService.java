package jnpf.service;

import jnpf.base.service.SuperService;
import jnpf.entity.UserEntity;

import java.util.List;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
public interface UserService extends SuperService<UserEntity> {

    /**
     *
     * @param idList
     * @return
     */
    List<UserEntity> getUserName(List<String> idList);

}
