package jnpf.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Method;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jnpf.base.ActionResult;
import jnpf.base.UserInfo;
import jnpf.base.service.SuperServiceImpl;
import jnpf.constant.CodeConst;
import jnpf.constant.MsgCode;
import jnpf.consts.ApiConst;
import jnpf.emnus.DataSetTypeEnum;
import jnpf.entity.ReportEntity;
import jnpf.entity.ReportVersionEntity;
import jnpf.entity.SystemEntity;
import jnpf.exception.DataException;
import jnpf.mapper.ReportMapper;
import jnpf.model.data.DataForm;
import jnpf.model.data.DataSetInfo;
import jnpf.model.data.DataSetPagination;
import jnpf.model.report.ReportCrForm;
import jnpf.model.report.ReportInfoVO;
import jnpf.model.report.ReportPagination;
import jnpf.model.report.ReportUpForm;
import jnpf.service.CodeNumService;
import jnpf.service.ReportService;
import jnpf.service.ReportVersionService;
import jnpf.service.SystemService;
import jnpf.util.*;
import jnpf.util.context.RequestContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Service
public class ReportServiceImpl extends SuperServiceImpl<ReportMapper, ReportEntity> implements ReportService {

    @Autowired
    private CodeNumService codeNumService;
    @Autowired
    private SystemService systemService;


    @Autowired
    private ReportVersionService versionService;

    @Override
    public List<ReportEntity> getList(ReportPagination pagination) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        String keyword = pagination.getKeyword();
        if (ObjectUtil.isNotEmpty(keyword)) {
            queryWrapper.lambda().and(t -> t.like(ReportEntity::getEnCode, keyword).or().like(ReportEntity::getFullName, keyword));
        }
        if (ObjectUtil.isNotEmpty(pagination.getCategory())) {
            queryWrapper.lambda().eq(ReportEntity::getCategory, pagination.getCategory());
        }
        if (ObjectUtil.isNotEmpty(pagination.getState())) {
            queryWrapper.lambda().eq(ReportEntity::getEnabledMark, pagination.getState());
        }
        if (ObjectUtil.isNotEmpty(pagination.getSystemId())) {
            queryWrapper.lambda().eq(ReportEntity::getSystemId, pagination.getSystemId());
        }
        queryWrapper.lambda().orderByAsc(ReportEntity::getSortCode).orderByDesc(ReportEntity::getCreatorTime);
        Page<ReportEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<ReportEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public void create(ReportCrForm form) {

        if (StringUtil.isEmpty(form.getEnCode())) {
            form.setEnCode(codeNumService.getCodeOnce(CodeConst.BB));
        }
        ReportEntity entity = JsonUtil.getJsonToBean(form, ReportEntity.class);
        UserInfo userInfo = UserProvider.getUser();
        this.creUpdateCheck(entity, true, true);
        String id = StringUtil.isNotEmpty(entity.getId()) ? entity.getId() : RandomUtil.uuId();
        entity.setId(id);
        entity.setEnabledMark(0);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setLastModifyUserId(null);
        entity.setLastModifyTime(null);
        this.setIgnoreLogicDelete().removeById(entity.getId());
        this.setIgnoreLogicDelete().saveOrUpdate(entity);
        this.clearIgnoreLogicDelete();
        form.setId(id);
        List<ReportVersionEntity> list = versionService.getList(id, ReportVersionEntity::getId);
        if (CollectionUtils.isEmpty(list)) {
            versionService.create(form);
        }
    }

    @Override
    public void update(String id, ReportEntity entity) {
        ReportEntity report = getById(id);
        if (StringUtil.isEmpty(entity.getEnCode())) {
            entity.setEnCode(codeNumService.getCodeOnce(CodeConst.BB));
        }

        this.creUpdateCheck(entity, !report.getFullName().equals(entity.getFullName()), !report.getEnCode().equals(entity.getEnCode()));
        entity.setId(id);
        updateById(entity);
    }

    @Override
    public ReportInfoVO getVersionInfo(String versionId) {
        ReportVersionEntity versionEntity = versionService.getById(versionId);
        ReportEntity entity = this.getById(versionEntity.getTemplateId());
        ReportInfoVO vo = JsonUtil.getJsonToBean(versionEntity, ReportInfoVO.class);
        vo.setVersionId(versionId);
        vo.setId(entity.getId());
        vo.setFullName(entity.getFullName());
        vo.setAllowExport(entity.getAllowExport());
        vo.setAllowWatermark(entity.getAllowWatermark());
        vo.setWatermarkConfig(entity.getWatermarkConfig());
        vo.setAllowPrint(entity.getAllowPrint());
        vo.setCategory(entity.getCategory());
        vo.setEnCode(entity.getEnCode());
        vo.setSortCode(entity.getSortCode());
        DataSetPagination pagination = new DataSetPagination();
        pagination.setObjectId(versionId);
        pagination.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
        String json = ReportUtil.http(ApiConst.DATASET_LIST, Method.GET, JsonUtil.entityToMap(pagination));
        ActionResult result = JsonUtil.getJsonToBean(json, ActionResult.class);
        List<DataSetInfo> dataSetList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(result.getData())) {
            dataSetList = JsonUtil.getJsonToList(result.getData(), DataSetInfo.class);
        }
        vo.setDataSetList(dataSetList);
        return vo;
    }

    @Override
    public void saveOrRelease(ReportUpForm form) {
        ReportVersionEntity versionEntity = versionService.getById(form.getVersionId());
        ReportVersionEntity versionNew = JsonUtil.getJsonToBean(form, ReportVersionEntity.class);
        versionNew.setFenceList(form.getColumnList());
        versionNew.setId(versionEntity.getId());
        versionNew.setState(versionEntity.getState());
        ReportEntity entity = this.getById(form.getId());
        //发布流程
        if (Objects.equals(form.getType(), 1)) {
            //改流程版本
            if (StringUtil.isNotEmpty(form.getVersionId())) {
                boolean isRelease = Objects.equals(versionNew.getState(), 2);
                ReportVersionEntity info = versionService.getList(form.getId(), ReportVersionEntity::getId, ReportVersionEntity::getState).stream().filter(t -> Objects.equals(t.getState(), 1)).findFirst().orElse(null);
                if (info != null) {
                    // 变更归档状态，排序码
                    info.setSortCode(0L);
                    info.setState(2);
                    versionService.updateById(info);
                }
                versionNew.setState(1);
                versionNew.setSortCode(1L);
                entity.setEnabledMark(1);
                if (isRelease) {
                    versionService.updateById(versionNew);
                    return;
                }
            }
        }
        versionService.updateById(versionNew);
        //数据集创建
        String versionId = versionNew.getId();
        List<DataSetInfo> dataSetList = form.getDataSetList() != null ? form.getDataSetList() : new ArrayList<>();
        DataForm dataForm = new DataForm();
        dataForm.setObjectId(versionId);
        dataForm.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
        dataForm.setList(dataSetList);
        ReportUtil.http(ApiConst.DATASET_SAVE, Method.POST, JsonUtil.entityToMap(dataForm));
        entity.setAllowExport(form.getAllowExport());
        entity.setAllowPrint(form.getAllowPrint());
        entity.setAllowWatermark(form.getAllowWatermark());
        entity.setWatermarkConfig(form.getWatermarkConfig());
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(UserProvider.getLoginUserId());
        this.updateById(entity);
    }

    @Override
    public List<ReportEntity> getTreeList() {
        SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
        if (infoByEnCode == null) {
            return new ArrayList<>();
        }
        QueryWrapper<ReportEntity> query = new QueryWrapper<>();
        query.lambda().eq(ReportEntity::getEnabledMark, 1);
        query.lambda().eq(ReportEntity::getSystemId, infoByEnCode.getId());
        query.lambda().orderByAsc(ReportEntity::getSortCode).orderByDesc(ReportEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public void delete(String id) {
        removeById(id);
        versionService.removeByTemplateId(id);
    }

    @Override
    public String importData(ReportInfoVO infoVO, Integer type, Boolean idCheck) {
        ReportEntity entity = JsonUtil.getJsonToBean(infoVO, ReportEntity.class);
        StringJoiner stringJoiner = new StringJoiner("、");
        //id为空切名称不存在时
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        if (idCheck) {
            queryWrapper.lambda().eq(ReportEntity::getId, entity.getId());
            if (this.getById(infoVO.getId()) != null) {
                if (Objects.equals(type, 1)) {
                    entity.setId(RandomUtil.uuId());
                } else {
                    stringJoiner.add("ID");
                }
            }
        }
        if (ObjectUtil.equal(type, 1)) {
            String copyNum = UUID.randomUUID().toString().substring(0, 5);
            entity.setFullName(entity.getFullName() + ".副本" + copyNum);
            entity.setEnCode(entity.getEnCode() + copyNum);
        } else if (ObjectUtil.equal(type, 0) && stringJoiner.length() > 0) {
            return stringJoiner + MsgCode.IMP007.get();
        }
        entity.setEnabledMark(0);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(UserProvider.getLoginUserId());
        entity.setLastModifyTime(null);
        entity.setLastModifyUserId(null);
        if (type != 1) {
            this.creUpdateCheck(entity, true, true);
        }
        this.setIgnoreLogicDelete().removeById(entity);
        this.setIgnoreLogicDelete().saveOrUpdate(entity);
        this.clearIgnoreLogicDelete();
        //版本添加
        ReportVersionEntity versionEntity = JsonUtil.getJsonToBean(infoVO, ReportVersionEntity.class);
        String versionId = RandomUtil.uuId();
        versionEntity.setId(versionId);
        versionEntity.setTemplateId(entity.getId());
        versionEntity.setCreatorUserId(UserProvider.getLoginUserId());
        versionEntity.setCreatorTime(new Date());
        versionEntity.setVersion(1);
        versionEntity.setState(0);
        versionEntity.setSortCode(0L);
        versionService.save(versionEntity);
        //数据集创建
        List<DataSetInfo> dataSetList = infoVO.getDataSetList() != null ? infoVO.getDataSetList() : new ArrayList<>();
        for (DataSetInfo dataSetInfo : dataSetList) {
            dataSetInfo.setId(null);
        }
        if (!dataSetList.isEmpty()) {
            DataForm dataForm = new DataForm();
            dataForm.setObjectId(versionId);
            dataForm.setObjectType(DataSetTypeEnum.REPORT_VER.getCode());
            dataForm.setList(dataSetList);
            ReportUtil.http(ApiConst.DATASET_SAVE, Method.POST, JsonUtil.entityToMap(dataForm));
        }
        return "";
    }

    @Override
    public Boolean isEncodeExist(String encode) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReportEntity::getEnCode, encode);
        if (!this.list(queryWrapper).isEmpty()) {
            return true;
        }
        return false;
    }

    public void creUpdateCheck(ReportEntity entity, Boolean fullNameCheck, Boolean encodeCheck) {
        String fullName = entity.getFullName();
        String encode = entity.getEnCode();
        String systemId = entity.getSystemId();
        // 名称长度验证
        if (fullName.length() > 80) {
            throw new DataException(MsgCode.EXIST005.get());
        }
        QueryWrapper<ReportEntity> query = new QueryWrapper<>();
        //重名验证
        if (fullNameCheck) {
            query.lambda().eq(ReportEntity::getFullName, fullName);
            query.lambda().eq(ReportEntity::getSystemId, systemId);
            if (!this.list(query).isEmpty()) {
                throw new DataException(MsgCode.EXIST003.get());
            }
        }
        //编码验证
        if (encodeCheck) {
            query.clear();
            query.lambda().eq(ReportEntity::getEnCode, encode);
            if (!this.list(query).isEmpty()) {
                throw new DataException(MsgCode.EXIST002.get());
            }
        }
    }

    @Override
    public List<ReportEntity> getListBySystemId(String systemId) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReportEntity::getSystemId, systemId);
        return this.list(queryWrapper);
    }

}
