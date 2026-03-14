package jnpf.model.data;

import lombok.Data;

import java.util.List;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
public class DataForm {
    private String ObjectType;
    private String ObjectId;
    private List<DataSetInfo> list;
}
