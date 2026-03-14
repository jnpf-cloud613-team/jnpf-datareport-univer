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
@Schema(description = "版本")
public class ReportVersionListVO {
    @Schema(description = "状态")
    private Integer state;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "版本")
    private String version;
}
