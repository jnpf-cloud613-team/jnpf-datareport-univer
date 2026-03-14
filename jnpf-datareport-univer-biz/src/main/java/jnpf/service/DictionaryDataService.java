package jnpf.service;

import jnpf.base.service.SuperService;
import jnpf.entity.DictionaryDataEntity;

import java.util.List;


/**
 * 字典数据
 *
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2019年9月27日 上午9:18
 */
public interface DictionaryDataService extends SuperService<DictionaryDataEntity> {

    List<DictionaryDataEntity> getDictionName(List<String> id);
}
