package jnpf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jnpf.base.service.SuperServiceImpl;
import jnpf.entity.UserEntity;
import jnpf.mapper.UserMapper;
import jnpf.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public List<UserEntity> getUserName(List<String> idList) {
        List<UserEntity> list = new ArrayList<>();
        if(idList.size()>0){
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(UserEntity::getId, idList);
            list = this.list(queryWrapper);
        }
        return list;
    }
}
