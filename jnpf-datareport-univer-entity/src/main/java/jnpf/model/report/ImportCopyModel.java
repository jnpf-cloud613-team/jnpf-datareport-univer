package jnpf.model.report;

import lombok.Data;

import java.util.List;

/**
 * 批量导入数据模型
 *
 * @author JNPF开发平台组
 * @version v6.0.0
 * @copyright 引迈信息技术有限公司
 * @date 2025/8/14 10:52:32
 */
@Data
public class ImportCopyModel {
    private List<ReportInfoVO> list;
    private String systemId;
}
