package jnpf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jnpf.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@TableName("report_version")
public class ReportVersionEntity  extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 主版本
     */
    @TableField("f_template_id")
    private String templateId;

    /**
     * 版本
     */
    @TableField("f_version")
    private Integer version;

    /**
     * 状态(0.设计中,1.启用中,2.已归档)
     */
    @TableField("f_state")
    private Integer state;

    /**
     * 模板json
     */
    @TableField("f_snapshot")
    private String snapshot;

    /**
     * 模板json
     */
    @TableField("f_cells")
    private String cells;

    /**
     * 模板json
     */
    @TableField("f_query_list")
    private String queryList;

    /**
     * 模板json
     */
    @TableField("f_convert_config")
    private String convertConfig;

    /**
     * 模板json
     */
    @TableField("F_COLUMN_LIST")
    private String columnList;

    /**
     * 模板json
     */
    @TableField("f_sort_list")
    private String sortList;

    /**
     * 分栏json
     */
    @TableField("f_fence_list")
    private String fenceList;

}
