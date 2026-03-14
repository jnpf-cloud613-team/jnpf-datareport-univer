package jnpf.model.data;

import lombok.Data;

import java.util.List;

/**
 * 可视化菜单对象
 *
 * @author JNPF开发平台组
 * @version V3.4
 * @copyright 引迈信息技术有限公司（https://www.jnpfsoft.com）
 * @date 2022/4/6
 */
@Data
public class MenuModel {
	/**
	 * 功能id
	 */
	private String id;

	/**
	 * 功能名
	 */
	private String fullName;

	/**
	 * 功能编码
	 */
	private String enCode;

	private Integer pc;

	private Integer app;

	private List<String> pcModuleParentId;

	private List<String> appModuleParentId;

	private Integer type;

	private String platformRelease;
}
