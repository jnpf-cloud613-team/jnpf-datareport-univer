package jnpf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jnpf.base.entity.SuperExtendEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@TableName("base_dictionary_data")
public class DictionaryDataEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 名称
     */
    @TableField("f_full_name")
    private String fullName;

}
