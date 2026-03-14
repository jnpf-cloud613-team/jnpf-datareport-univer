package jnpf.service;

import jnpf.base.service.SuperService;
import jnpf.entity.ReportEntity;
import jnpf.model.report.ReportCrForm;
import jnpf.model.report.ReportInfoVO;
import jnpf.model.report.ReportPagination;
import jnpf.model.report.ReportUpForm;

import java.util.List;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
public interface ReportService extends SuperService<ReportEntity> {

    /**
     * 列表
     *
     * @return 实体类
     */
    List<ReportEntity> getList(ReportPagination pagination);

    /**
     * 创建
     */
    void create(ReportCrForm form);

    /**
     * 更新
     */
    void update(String id, ReportEntity entity);

    /**
     * 获取详情
     *
     * @return ReportInfoVO
     */
    ReportInfoVO getVersionInfo(String versionId);

    /**
     * 保存或者发布 通过type：0-保存，1-发布
     *
     * @param form
     */
    void saveOrRelease(ReportUpForm form);

    /**
     * 下拉选择
     */
    List<ReportEntity> getTreeList();

    /**
     * 删除
     *
     * @param id
     */
    void delete(String id);

    /**
     * 导入模板
     *
     * @param infoVO
     * @param type
     * @return
     */
    String importData(ReportInfoVO infoVO, Integer type, Boolean idCheck);

    Boolean isEncodeExist(String encode);

    /**
     * 根据系统id获取列表
     *
     * @param systemId
     * @return
     */
    List<ReportEntity> getListBySystemId(String systemId);
}
