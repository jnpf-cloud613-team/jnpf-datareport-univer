package jnpf.model.report;

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
@Schema(description = "分页模型")
public class ReportPagination extends Pagination {
    @Schema(description = "分类")
    private String category;
    @Schema(description = "标志")
    private Integer state;
    private String sheetId;
    private String snapshot;
    private String systemId;
    private String fullName;
    private String id;
    private String imgValue;
    private String imgType;
}
