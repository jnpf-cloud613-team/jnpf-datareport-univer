package jnpf.model.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jnpf.model.data.DataSetInfo;
import lombok.Data;

import java.util.List;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@Schema(description = "基础信息")
public class ReportCrForm {

    @Schema(description = "主键id")
    private String id;

    @Schema(description = "应用编码")
    private String systemId;


    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "编码")
    private String enCode;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "模板内容")
    private String cells;

    @Schema(description = "模板内容")
    private String snapshot;

    @Schema(description = "模板内容")
    private String queryList;

    @Schema(description = "模板内容")
    private String convertConfig;

    @Schema(description = "模板内容")
    private String sortList;

    @Schema(description = "导出")
    private Integer allowExport;

    @Schema(description = "打印")
    private Integer allowPrint;

    @Schema(description = "是否支持水印")
    private Integer allowWatermark;

    @Schema(description = "水印配置")
    private String watermarkConfig;

    @Schema(description = "分栏配置")
    private String columnList;

    @Schema(description = "数据集")
    private List<DataSetInfo> dataSetList;
}
