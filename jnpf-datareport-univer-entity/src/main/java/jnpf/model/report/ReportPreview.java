package jnpf.model.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/17 下午1:58
 */
@Data
public class ReportPreview {

    @Schema(description = "模板内容")
    private String customs;

    @Schema(description = "模板内容")
    private String snapshot;

}
