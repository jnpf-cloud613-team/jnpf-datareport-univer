package jnpf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jnpf.base.entity.SuperExtendEntity;
import lombok.Data;


/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@TableName("base_user")
public class UserEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {
    /**
     * 账户
     */
    @TableField("F_ACCOUNT")
    private String account;

    /**
     * 姓名
     */
    @TableField("F_REAL_NAME")
    private String realName;

}
