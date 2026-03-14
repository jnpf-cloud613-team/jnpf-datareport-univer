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
@Schema(description = "表单基础信息")
public class ReportUpForm extends ReportCrForm {

    @Schema(description = "版本id")
    private String versionId;

    @Schema(description = "动作类型：0-保存，1-发布")
    private Integer type;
}
