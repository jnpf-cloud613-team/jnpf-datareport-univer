package jnpf.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jnpf.base.Pagination;
import lombok.Data;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@Schema(description = "数据集列表参数")
public class DataSetPagination extends Pagination {
    @Schema(description = "数据集数据类型：参考枚举DataSetTypeEnum")
    private String objectType;
    @Schema(description = "数据集数据id")
    private String objectId;
}
