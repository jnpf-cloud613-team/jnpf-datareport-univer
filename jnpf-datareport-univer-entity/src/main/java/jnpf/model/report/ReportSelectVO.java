package jnpf.model.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
public class ReportSelectVO {
    @Schema(description = "主键id")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "是否有子集")
    private Boolean hasChildren;
    @Schema(description = "子集对象")
    private List<ReportSelectVO> children;
}
