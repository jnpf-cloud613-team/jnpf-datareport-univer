package jnpf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jnpf.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@TableName("report_template")
public class ReportEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 主版本
     */
    @TableField("f_version_id")
    private String versionId;

    /**
     * 名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 导出
     */
    @TableField("F_ALLOW_EXPORT")
    private Integer allowExport;

    /**
     * 打印
     */
    @TableField("F_ALLOW_PRINT")
    private Integer allowPrint;

    /**
     * 水印印
     */
    @TableField("F_ALLOW_WATERMARK")
    private Integer allowWatermark;

    /**
     * 水印配置
     */
    @TableField("F_WATERMARK_CONFIG")
    private String watermarkConfig;

    /**
     * 发布时勾选平台类型
     */
    @TableField("F_PLATFORM_RELEASE")
    private String platformRelease;

    @TableField("F_SYSTEM_ID")
    private String systemId;

}
