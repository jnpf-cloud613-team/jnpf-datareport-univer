package jnpf.model.report;

import com.baomidou.mybatisplus.annotation.TableField;
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
@Schema(description = "模板详情")
public class ReportInfoVO {

    @Schema(description = "模板id")
    private String id;

    @Schema(description = "版本id")
    private String versionId;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "编码")
    private String enCode;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "模板内容")
    private String cells;

    @Schema(description = "模板内容")
    private String snapshot;

    @Schema(description = "搜索列表")
    private String queryList;

    @Schema(description = "转换配置")
    private String convertConfig;

    @Schema(description = "排序")
    private String sortList;

    @Schema(description = "应用id")
    private String systemId;

    @Schema(description = "是否导出")
    private Integer allowExport;

    @Schema(description = "是否打印")
    private Integer allowPrint;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "发布时勾选平台类型" )
    private String platformRelease;

    @Schema(description = "pc已发布菜单名称" )
    private String pcReleaseName;

    @Schema(description = "app已发布菜单名称" )
    private String appReleaseName;

    @Schema(description = "pc是否发布" )
    private Integer pcIsRelease;

    @Schema(description = "app是否发布" )
    private Integer appIsRelease;

    @Schema(description = "水印开关" )
    private Integer allowWatermark;

    @Schema(description = "水印设置" )
    private String watermarkConfig;

    @Schema(description = "分类设置" )
    private String columnList;

    @Schema(description = "模板内容")
    private List<DataSetInfo> dataSetList;
}
