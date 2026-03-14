package jnpf.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jnpf.model.report.TableTreeModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
@Schema(description = "数据集合详情")
public class DataSetInfo implements Serializable {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "关联数据类型")
    private String objectType;

    @Schema(description = "关联数据类型")
    private String objectId;

    @NotBlank
    @Schema(description = "数据集名称")
    private String fullName;

    @NotBlank
    @Schema(description = "数据库连接")
    private String dbLinkId;

    @Schema(description = "数据sql语句")
    private String dataConfigJson;

    @Schema(description = "参数json")
    private String parameterJson;

    @Schema(description = "字段json")
    private String fieldJson;

    @Schema(description = "类型：1-sql语句，2-配置式")
    private Integer type;

    @Schema(description = "sql语句")
    private String visualConfigJson;

    @Schema(description = "配置式json")
    private String filterConfigJson;

    @Schema(description = "数据接口名称")
    private String treePropsName;

    @Schema(description = "数据接口id")
    private String interfaceId;

    @Schema(description = "字段信息")
    private List<TableTreeModel> children;

    @Schema(description = "结果集筛选 1-所有数据，2-前n条数据，3-后n条数据，4-奇数条数据，5-偶数条数据，6-指定数据")
    private String resultFilter;

    @Schema(description = "用户指定数据")
    private String specifiedData;
}
