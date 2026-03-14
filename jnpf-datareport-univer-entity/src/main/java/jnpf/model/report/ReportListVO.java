package jnpf.model.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
public class ReportListVO {
    @Schema(description = "主键id")
    private String id;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "编码")
    private String enCode;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "创建人")
    private String creatorUser;

    @Schema(description = "创建时间")
    private Long creatorTime;

    @Schema(description = "修改人")
    private String lastModifyUser;

    @Schema(description = "修改时")
    private Long lastModifyTime;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "有效标志")
    private Integer enabledMark;

    @Schema(description = "状态")
    private Integer state;
}
