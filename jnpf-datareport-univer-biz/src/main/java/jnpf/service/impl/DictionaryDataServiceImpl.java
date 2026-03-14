package jnpf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jnpf.base.service.SuperServiceImpl;
import jnpf.entity.DictionaryDataEntity;
import jnpf.mapper.DictionaryDataMapper;
import jnpf.service.DictionaryDataService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 字典数据
 *
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class DictionaryDataServiceImpl extends SuperServiceImpl<DictionaryDataMapper, DictionaryDataEntity> implements DictionaryDataService {

    @Override
    public List<DictionaryDataEntity> getDictionName(List<String> id) {
        List<DictionaryDataEntity> dictionList = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().and(
                    t -> t.in(DictionaryDataEntity::getId, id)
            );
            dictionList = this.list(queryWrapper);
        }
        return dictionList;
    }

}
