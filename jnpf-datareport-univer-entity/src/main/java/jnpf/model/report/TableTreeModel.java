package jnpf.model.report;

import jnpf.util.treeutil.SumTree;
import lombok.Data;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Data
public class TableTreeModel extends SumTree<TableTreeModel> {

    private String fullName;

    private String label;

}
