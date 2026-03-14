package jnpf.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.collect.ImmutableList;
import jnpf.base.ActionResult;
import jnpf.base.service.SuperServiceImpl;
import jnpf.constant.FileTypeConstant;
import jnpf.constant.MsgCode;
import jnpf.consts.ApiConst;
import jnpf.emnus.DataSetTypeEnum;
import jnpf.entity.FileParameter;
import jnpf.entity.ReportEntity;
import jnpf.entity.ReportVersionEntity;
import jnpf.exception.DataException;
import jnpf.mapper.ReportVersionMapper;
import jnpf.model.DataQuery;
import jnpf.model.data.*;
import jnpf.model.report.ReportCrForm;
import jnpf.model.report.ReportPagination;
import jnpf.model.report.UploaderVO;
import jnpf.service.ReportService;
import jnpf.service.ReportVersionService;
import jnpf.univer.chart.UniverChartModel;
import jnpf.univer.data.custom.UniverCustom;
import jnpf.univer.model.UniverPreview;
import jnpf.univer.model.UniverWorkBook;
import jnpf.util.*;
import lombok.Cleanup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Service
public class ReportVersionServiceImpl extends SuperServiceImpl<ReportVersionMapper, ReportVersionEntity> implements ReportVersionService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ReportService reportService;

    @Override
    @DSTransactional
    public void create(ReportCrForm form) {
        String versionId = RandomUtil.uuId();
        ReportVersionEntity entity = JsonUtil.getJsonToBean(form, ReportVersionEntity.class);
        entity.setId(versionId);
        entity.setTemplateId(form.getId());
        entity.setCreatorUserId(UserProvider.getLoginUserId());
        entity.setCreatorTime(new Date());
        List<ReportVersionEntity> verList = getList(entity.getId(), ReportVersionEntity::getVersion);
        int version = verList.stream().map(ReportVersionEntity::getVersion).max(Comparator.naturalOrder()).orElse(0) + 1;
        entity.setVersion(version);
        entity.setState(0);
        entity.setSortCode(0l);
        this.setIgnoreLogicDelete().removeById(entity.getId());
        this.setIgnoreLogicDelete().saveOrUpdate(entity);
        this.clearIgnoreLogicDelete();
        List<DataSetInfo> dataSetList = form.getDataSetList() != null ? form.getDataSetList() : new ArrayList<>();
        if (dataSetList.size() > 0) {
            //数据集创建
            DataForm dataForm = new DataForm();
            dataForm.setObjectId(versionId);
            dataForm.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
            dataForm.setList(dataSetList);
            ReportUtil.http(ApiConst.DATASET_SAVE, Method.POST, JsonUtil.entityToMap(dataForm));
        }
    }

    @Override
    public List<ReportVersionEntity> getList(String templateId, SFunction<ReportVersionEntity, ?>... columns) {
        QueryWrapper<ReportVersionEntity> queryWrapper = new QueryWrapper<>();
        if (columns != null && columns.length > 0) {
            queryWrapper.lambda().select(columns);
        }
        queryWrapper.lambda().eq(ReportVersionEntity::getTemplateId, templateId);
        queryWrapper.lambda().orderByDesc(ReportVersionEntity::getSortCode).orderByAsc(ReportVersionEntity::getState);
        return this.list(queryWrapper);
    }

    @Override
    public String copyVersion(String versionId) {
        ReportVersionEntity entity = this.getById(versionId);
        ReportVersionEntity versionEntity = JsonUtil.getJsonToBean(entity, ReportVersionEntity.class);
        String newVersionId = RandomUtil.uuId();
        versionEntity.setId(newVersionId);
        List<ReportVersionEntity> verList = getList(entity.getTemplateId(), ReportVersionEntity::getVersion);
        int version = verList.stream().map(ReportVersionEntity::getVersion).max(Comparator.naturalOrder()).orElse(0) + 1;
        versionEntity.setVersion(version);
        versionEntity.setState(0);
        versionEntity.setSortCode(0l);
        versionEntity.setCreatorTime(new Date());
        versionEntity.setCreatorUserId(UserProvider.getLoginUserId());
        versionEntity.setLastModifyTime(null);
        versionEntity.setLastModifyUserId(null);
        DataSetPagination pagination = new DataSetPagination();
        pagination.setObjectId(versionId);
        pagination.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
        String json = ReportUtil.http(ApiConst.DATASET_LIST, Method.GET, JsonUtil.entityToMap(pagination));
        ActionResult result = JsonUtil.getJsonToBean(json, ActionResult.class);
        List<DataSetInfo> dataSetList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(result.getData())) {
            dataSetList.addAll(JsonUtil.getJsonToList(result.getData(), DataSetInfo.class));
        }
        if (dataSetList.size() > 0) {
            for (DataSetInfo item : dataSetList) {
                item.setId(null);
                item.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
                item.setObjectId(newVersionId);
            }
            DataForm dataForm = new DataForm();
            dataForm.setObjectId(newVersionId);
            dataForm.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
            dataForm.setList(dataSetList);
            ReportUtil.http(ApiConst.DATASET_SAVE, Method.POST, JsonUtil.entityToMap(dataForm));
        }
        this.save(versionEntity);
        return newVersionId;
    }

    @Override
    public void removeByTemplateId(String templateId) {
        QueryWrapper<ReportVersionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReportVersionEntity::getTemplateId, templateId);
        this.remove(queryWrapper);
    }

    @Override
    public UniverPreview preview(String id, ReportPagination pagination, Map<String, Object> params) {
        ReportVersionEntity entity = getById(id);
        if (ObjectUtil.isEmpty(entity)) {
            throw new DataException(MsgCode.FA015.get());
        }
        if (ObjectUtil.isEmpty(entity.getSnapshot())) {
            throw new DataException(MsgCode.FA105.get());
        }
        //获取当前
        UniverWorkBook univerWorkBook = JsonUtil.getJsonToBean(entity.getSnapshot(), UniverWorkBook.class);
        List<String> sheetOrder = univerWorkBook.getSheetOrder();
        ReportEntity report = reportService.getById(entity.getTemplateId());
        Map<String, Map<String, List<Map<String, Object>>>> sheetData = new HashMap<>();
        Map<String, Map<String, Map<String, Object>>> parameterData = new HashMap<>();
        //当前数据
        String sheet = StringUtil.isNotEmpty(pagination.getSheetId()) ? pagination.getSheetId() : !sheetOrder.isEmpty() ? sheetOrder.get(0) : null;
        for (String sheetId : sheetOrder) {
            DataSetQuery query = new DataSetQuery();
            query.setModuleId(entity.getTemplateId());
            query.setId(id);
            Map<String, Object> dataMap = new HashMap<>(params);
            query.setMap(dataMap);
            query.setType(DataSetTypeEnum.REPORT_VER.getCode());
            query.setSnowFlakeId(RandomUtil.uuId());
            query.setConvertConfig(entity.getConvertConfig());
            List<DataQuery> dataQueryList = StringUtil.isNotEmpty(entity.getQueryList()) ? JsonUtil.getJsonToList(entity.getQueryList(), DataQuery.class) : new ArrayList<>();
            Map<String, List<DataQuery>> queryMap = dataQueryList.stream().collect(Collectors.groupingBy(DataQuery::getSheet));
            List<DataQuery> queryListAll = queryMap.get(sheetId) != null ? queryMap.get(sheetId) : new ArrayList<>();
            List<Object> queryList = new ArrayList<>();
            if (Objects.equals(sheetId, sheet)) {
                for (DataQuery dataQuery : queryListAll) {
                    queryList.addAll(dataQuery.getQueryList());
                }
            }
            query.setQueryList(JSONUtil.toJsonStr(queryList));
            String sheetJson = ReportUtil.http(ApiConst.DATASET_DATA, Method.POST, JsonUtil.entityToMap(query));
            if (StringUtil.isNotEmpty(sheetJson)) {
                Map<String, List<Map<String, Object>>> dataList = new HashMap<>();
                try {
                    ActionResult result = JsonUtil.getJsonToBean(sheetJson, ActionResult.class);
                    if (ObjectUtil.isNotEmpty(result) && result.getData() instanceof Map) {
                        Map<String, List<Map<String, Object>>> data = JsonUtil.getJsonToBean(result.getData(), Map.class);
                        dataList.putAll(data);
                    }
                } catch (Exception e) {}
                sheetData.put(sheetId, dataList);
            }

            //参数数据
            String parameterJson = ReportUtil.http(ApiConst.PARAMETER_DATA, Method.POST, JsonUtil.entityToMap(query));
            if (StringUtil.isNotEmpty(parameterJson)) {
                Map<String, Map<String, Object>> dataList = new HashMap<>();
                try {
                    ActionResult result = JsonUtil.getJsonToBean(parameterJson, ActionResult.class);
                    if (ObjectUtil.isNotEmpty(result) && result.getData() instanceof Map) {
                        Map data = JsonUtil.getJsonToBean(result.getData(), Map.class);
                        dataList.putAll(data);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                parameterData.put(sheetId, dataList);
            }
        }
        UniverConvert convert = new UniverConvert();
        UniverPreview vo = convert.transform(entity.getSnapshot(), entity.getCells(), entity.getSortList(), entity.getFenceList(),sheetData, parameterData);
        vo.setVersionId(id);
        vo.setQueryList(entity.getQueryList());
        vo.setAllowWatermark(report.getAllowWatermark());
        vo.setWatermarkConfig(report.getWatermarkConfig());
        vo.setFullName(report.getFullName());
        vo.setAllowExport(report.getAllowExport());
        vo.setAllowPrint(report.getAllowPrint());
        return vo;
    }

    @Override
    public UniverPreview previewTemplate(String id, ReportPagination pagination, Map<String, Object> params) {
        ReportVersionEntity entity = getList(id, ReportVersionEntity::getId, ReportVersionEntity::getState).stream().filter(t -> Objects.equals(t.getState(), 1)).findFirst().orElse(null);
        if (ObjectUtil.isEmpty(entity)) {
            throw new DataException(MsgCode.FA015.get());
        }
        return preview(entity.getId(), pagination, params);
    }

    @Override
    public UniverPreview importExcel(MultipartFile multipartFile) throws IOException {
        UniverCustom cellData = new UniverCustom();
        UniverWorkBook univerWorkBook = UniverExcel.formFile(multipartFile);
        UniverPreview vo = new UniverPreview();
        vo.setSnapshot(JSONUtil.toJsonStr(univerWorkBook));
        vo.setCells(JSONUtil.toJsonStr(cellData));
        return vo;
    }

    @Override
    public UploaderVO downExcel(ReportPagination pagination, Map<String, Object> params) {
        String id = pagination.getId();
        UploaderVO vo = new UploaderVO();
        ReportVersionEntity versionEntity = getById(id);
        if (ObjectUtil.isEmpty(versionEntity)) {
            return vo;
        }
        ReportEntity entity = reportService.getById(versionEntity.getTemplateId());
        UniverPreview preview = preview(id, pagination, params);
        pagination.setSnapshot(preview.getSnapshot());
        pagination.setFullName(entity.getFullName());
        vo = downExcel(pagination);
        return vo;
    }

    @Override
    public UploaderVO downExcel(ReportPagination pagination) {
        UploaderVO vo = new UploaderVO();
        if (StringUtil.isNotEmpty(pagination.getSnapshot())) {
            try {
                List<UniverChartModel> chartList = new ArrayList<>();
                @Cleanup XSSFWorkbook workbook = new XSSFWorkbook();
                List<String> sheetList = Arrays.asList(pagination.getSheetId().split(","));
                UniverExcel.downExcel(pagination.getSnapshot(), chartList, workbook, sheetList);
                String fileName = pagination.getFullName() + ".xlsx";
                @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                String url = "/api/Report/data/Download?name=" + fileName + "&encryption=";
                FileInfo fileInfo = FileUploadUtils.uploadFile(new FileParameter(FileTypeConstant.TEMPORARY, fileName), outputStream.toByteArray());
                vo.setName(fileInfo.getFilename());
                vo.setUrl(UploaderUtil.uploaderFile(url, fileInfo.getFilename() + "#" + FileTypeConstant.TEMPORARY));
            } catch (Exception e) {
                log.error("报表导出excel异常：" + e.getMessage());
                throw new DataException(MsgCode.FA107.get());
            }
        }
        return vo;
    }

}
