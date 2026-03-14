package jnpf.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import jnpf.base.Pagination;
import jnpf.base.service.SuperService;
import jnpf.entity.ReportVersionEntity;
import jnpf.model.report.ReportCrForm;
import jnpf.model.report.ReportPagination;
import jnpf.model.report.UploaderVO;
import jnpf.univer.model.UniverPreview;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
public interface ReportVersionService extends SuperService<ReportVersionEntity> {

    /**
     * 创建版本
     *
     * @param form
     */
    void create(ReportCrForm form);

    /**
     * 获取版本列表
     *
     * @param templateId
     * @return
     */
    List<ReportVersionEntity> getList(String templateId, SFunction<ReportVersionEntity, ?>... columns);

    /**
     * 复制版本（点击新增版本）
     *
     * @param versionId
     */
    String copyVersion(String versionId);

    /**
     * 根据id删除版本
     *
     * @param templateId
     * @return
     */
    void removeByTemplateId(String templateId);

    /**
     * 预览数据
     *
     * @return
     */
    UniverPreview preview(String id, ReportPagination pagination, Map<String, Object> params);

    /**
     * 预览数据
     *
     * @return
     */
    UniverPreview previewTemplate(String id, ReportPagination pagination, Map<String, Object> params);

    /**
     * 导入excel
     *
     * @param multipartFile
     */
    UniverPreview importExcel(MultipartFile multipartFile) throws IOException;

    /**
     * 导出excel
     *
     */
    UploaderVO downExcel(ReportPagination pagination, Map<String, Object> params);

    /**
     * 导出excel
     *
     * @param pagination
     */
    UploaderVO downExcel(ReportPagination pagination);
}
