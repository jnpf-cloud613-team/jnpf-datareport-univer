package jnpf.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.Method;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jnpf.base.ActionResult;
import jnpf.base.vo.DownloadVO;
import jnpf.base.vo.ListVO;
import jnpf.base.vo.PageListVO;
import jnpf.base.vo.PaginationVO;
import jnpf.constant.CodeConst;
import jnpf.constant.FileTypeConstant;
import jnpf.constant.GlobalConst;
import jnpf.constant.MsgCode;
import jnpf.consts.ApiConst;
import jnpf.emnus.ModuleTypeEnum;
import jnpf.entity.*;
import jnpf.exception.DataException;
import jnpf.model.data.DataSetInfo;
import jnpf.model.data.MenuModel;
import jnpf.model.data.ModuleNameVO;
import jnpf.model.report.*;
import jnpf.service.*;
import jnpf.util.*;
import jnpf.util.context.RequestContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Tag(name = "报表信息", description = "Report")
@RestController
@RequestMapping("/api/Report")
@Log4j2
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private ReportVersionService versionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CodeNumService codeNumService;


    @Operation(summary = "列表")
    @GetMapping
    public ActionResult<PageListVO<ReportListVO>> list(ReportPagination paginationPrint) {
        SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
        if (BeanUtil.isNotEmpty(infoByEnCode)) {
            paginationPrint.setSystemId(infoByEnCode.getId());
        }
        List<ReportEntity> list = reportService.getList(paginationPrint);
        List<String> userId = new ArrayList<>();
        userId.addAll(list.stream().map(ReportEntity::getCreatorUserId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
        userId.addAll(list.stream().map(ReportEntity::getLastModifyUserId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
        List<UserEntity> userList = userService.getUserName(userId);
        List<String> dictionary = list.stream().map(ReportEntity::getCategory).collect(Collectors.toList());
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getDictionName(dictionary);
        List<ReportListVO> listVOS = new ArrayList<>();
        for (ReportEntity entity : list) {
            ReportListVO vo = JsonUtil.getJsonToBean(entity, ReportListVO.class);
            vo.setState(vo.getEnabledMark());
            DictionaryDataEntity dataEntity = dictionList.stream().filter(t -> t.getId().equals(entity.getCategory())).findFirst().orElse(null);
            vo.setCategory(dataEntity != null ? dataEntity.getFullName() : "");
            //创建者
            UserEntity creatorUser = userList.stream().filter(t -> t.getId().equals(entity.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : entity.getCreatorUserId());
            //修改人
            UserEntity lastModifyUser = userList.stream().filter(t -> t.getId().equals(entity.getLastModifyUserId())).findFirst().orElse(null);
            vo.setLastModifyUser(lastModifyUser != null ? lastModifyUser.getRealName() + "/" + lastModifyUser.getAccount() : entity.getLastModifyUserId());
            listVOS.add(vo);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationPrint, PaginationVO.class);
        return ActionResult.page(listVOS, paginationVO);
    }

    @PostMapping
    @Operation(summary = "新建")
    @Parameters({
            @Parameter(name = "form", description = "模型", required = true),
    })
    public ActionResult create(@RequestBody ReportCrForm form) {
        form.setAllowExport(1);
        form.setAllowPrint(1);
        SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
        if (BeanUtil.isNotEmpty(infoByEnCode)) {
            form.setSystemId(infoByEnCode.getId());
        }

        reportService.create(form);
        return ActionResult.success(MsgCode.SU001.get(), form.getId());
    }

    @Operation(summary = "详情")
    @Parameters({
            @Parameter(name = "id", description = "模板id")
    })
    @GetMapping("/{id}")
    public ActionResult<ReportInfoVO> info(@PathVariable("id") String id) {
        ReportEntity byId = reportService.getById(id);
        ReportInfoVO vo = JsonUtil.getJsonToBean(byId, ReportInfoVO.class);
        return ActionResult.success(vo);
    }

    @Operation(summary = "更新")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "form", description = "模型", required = true),
    })
    public ActionResult update(@PathVariable("id") String id, @RequestBody ReportUpForm form) {
        ReportEntity entity = JsonUtil.getJsonToBean(form, ReportEntity.class);
        SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
        if (BeanUtil.isNotEmpty(infoByEnCode)) {
            entity.setSystemId(infoByEnCode.getId());
        }
        reportService.update(id, entity);
        return ActionResult.success(MsgCode.SU004.get());
    }

    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "模板id", required = true)
    })
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable String id) {
        if (reportService.getById(id) != null) {
            reportService.delete(id);
            return ActionResult.success(MsgCode.SU003.get());
        } else {
            return ActionResult.fail(MsgCode.FA003.get());
        }
    }

    /*============版本增删改==============*/
    @Operation(summary = "版本详情")
    @Parameters({
            @Parameter(name = "versionId", description = "版本id", required = true)
    })
    @GetMapping("/Info/{versionId}")
    public ActionResult<ReportInfoVO> versionInfo(@PathVariable String versionId) {
        ReportInfoVO info = reportService.getVersionInfo(versionId);
        return ActionResult.success(info);
    }

    @Operation(summary = "版本新增")
    @Parameters({
            @Parameter(name = "versionId", description = "版本id", required = true)
    })
    @PostMapping("/Info/{versionId}")
    public ActionResult copyVersion(@PathVariable String versionId) {
        String newVersionId = versionService.copyVersion(versionId);
        return ActionResult.success(MsgCode.SU005.get(), newVersionId);
    }

    @Operation(summary = "版本删除")
    @Parameters({
            @Parameter(name = "versionId", description = "版本id", required = true)
    })
    @DeleteMapping("/Info/{versionId}")
    public ActionResult deleteVersion(@PathVariable String versionId) {
        ReportVersionEntity entity = versionService.getById(versionId);
        if (entity != null) {
            List<ReportVersionEntity> list = versionService.getList(entity.getTemplateId(), ReportVersionEntity::getId);
            if (list.size() == 1) {
                return ActionResult.fail(MsgCode.SYS043.get());
            }
            if (Objects.equals(entity.getState(), 1)) {
                return ActionResult.fail(MsgCode.SYS044.get());
            }
            if (Objects.equals(entity.getState(), 2)) {
                return ActionResult.fail(MsgCode.SYS045.get());
            }
            versionService.removeById(versionId);
        }
        return ActionResult.success(MsgCode.SU003.get());
    }

    @Operation(summary = "版本列表")
    @Parameters({
            @Parameter(name = "id", description = "模板id", required = true)
    })
    @GetMapping("/Version/{id}")
    public ActionResult<List<ReportVersionListVO>> versionList(@PathVariable String id) {
        List<ReportVersionEntity> list = versionService.getList(id, ReportVersionEntity::getId, ReportVersionEntity::getState, ReportVersionEntity::getVersion);
        List<ReportVersionListVO> listVO = new ArrayList<>();
        for (ReportVersionEntity jsonEntity : list) {
            ReportVersionListVO vo = JsonUtil.getJsonToBean(jsonEntity, ReportVersionListVO.class);
            vo.setFullName("报表版本V" + vo.getVersion());
            listVO.add(vo);
        }
        if (listVO.isEmpty()) {
            return ActionResult.fail(MsgCode.PRI008.get());
        }
        return ActionResult.success(listVO);
    }

    @Operation(summary = "保存或者发布")
    @PostMapping("/Save")
    public ActionResult saveOrRelease(@RequestBody ReportUpForm form) {
        reportService.saveOrRelease(form);
        if (Objects.equals(form.getType(), 1)) {
            return ActionResult.success(MsgCode.SU011.get());
        }
        return ActionResult.success(MsgCode.SU002.get());
    }

    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "模板id", required = true)
    })
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult copy(@PathVariable String id) {
        ReportEntity entity = reportService.getById(id);
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        String fullName = entity.getFullName() + ".副本" + copyNum;
        if (fullName.length() > 50) {
            return ActionResult.fail(MsgCode.PRI006.get());
        }
        List<ReportVersionEntity> list = versionService.getList(id, ReportVersionEntity::getId);
        ReportInfoVO info = new ReportInfoVO();
        List<DataSetInfo> listVO = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            info = reportService.getVersionInfo(list.get(0).getId());
            List<DataSetInfo> dataSetList = info.getDataSetList() != null ? info.getDataSetList() : new ArrayList<>();
            for (DataSetInfo dataSetInfo : dataSetList) {
                dataSetInfo.setObjectId(null);
                dataSetInfo.setId(null);
                listVO.add(dataSetInfo);
            }
        }
        ReportCrForm form = JsonUtil.getJsonToBean(info, ReportCrForm.class);
        form.setFullName(fullName);
        form.setEnCode(entity.getEnCode() + copyNum);
        form.setCategory(entity.getCategory());
        form.setSortCode(entity.getSortCode());
        form.setDescription(entity.getDescription());
        form.setId(null);
        SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
        if (BeanUtil.isNotEmpty(infoByEnCode)) {
            form.setSystemId(infoByEnCode.getId());
        }
        reportService.create(form);
        return ActionResult.success(MsgCode.SU007.get());
    }

    @Operation(summary = "导出")
    @Parameters({
            @Parameter(name = "id", description = "模板id")
    })
    @GetMapping("/{id}/Actions/Export")
    public ActionResult<DownloadVO> export(@PathVariable String id) {
        DownloadVO vo = new DownloadVO();
        ReportEntity entity = reportService.getById(id);
        List<ReportVersionEntity> list = versionService.getList(id, ReportVersionEntity::getId);
        if (CollectionUtils.isEmpty(list)) {
            throw new DataException(MsgCode.FA001.get());
        }
        ReportInfoVO info = reportService.getVersionInfo(list.get(0).getId());
        if (StringUtil.isEmpty(info.getSystemId())) {
            SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
            if (BeanUtil.isNotEmpty(infoByEnCode)) {
                info.setSystemId(infoByEnCode.getId());
            }
        }
        String json = JsonUtil.getObjectToString(info);
        String tableName = ModuleTypeEnum.REPORT_TEMPLATE.getTableName();
        String fileName = entity.getFullName() + "_" + DateUtil.dateFormatByPattern(new Date(), "yyyyMMddHHmmss") + "." + tableName;
        try {
            String url = "/api/Report/data/Download?name=" + fileName + "&encryption=";
            FileInfo fileInfo = FileUploadUtils.uploadFile(new FileParameter(FileTypeConstant.TEMPORARY, fileName), json.getBytes(GlobalConst.DEFAULT_CHARSET));
            vo.setName(fileInfo.getFilename());
            vo.setUrl(UploaderUtil.uploaderFile(url, fileInfo.getFilename() + "#" + FileTypeConstant.TEMPORARY));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "导入")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult importData(@RequestPart("file") MultipartFile multipartFile,
                                   @RequestParam("type") Integer type) throws DataException {
        //判断是否为.rp结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.REPORT_TEMPLATE.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        SystemEntity infoByEnCode = systemService.getInfoByEnCode(RequestContext.getAppCode());
        //读取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        ReportInfoVO infVo = JsonUtil.getJsonToBean(fileContent, ReportInfoVO.class);
        //判断Id是否重复
        boolean idCheck = false;

        if (type == 0 && (StringUtil.isEmpty(infVo.getSystemId())
                || !infVo.getSystemId().equals(infoByEnCode.getId()))) {
            infVo.setId(RandomUtil.uuId());
            infVo.setEnCode(codeNumService.getCodeFunction(() ->
                    codeNumService.getCodeOnce(CodeConst.BB), encode -> reportService.isEncodeExist(encode)));
        } else {
            idCheck = true;
        }
        infVo.setSystemId(infoByEnCode.getId());
        String str = reportService.importData(infVo, type, idCheck);
        if (StringUtil.isNotEmpty(str)) {
            return ActionResult.fail(str);
        }
        return ActionResult.success(MsgCode.IMP001.get());
    }

    @Operation(summary = "下拉列表")
    @GetMapping("/Selector")
    public ActionResult<ListVO<ReportSelectVO>> selectorList() {
        List<ReportEntity> list = reportService.getTreeList();
        List<String> dictionary = list.stream().map(ReportEntity::getCategory).collect(Collectors.toList());
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getDictionName(dictionary);
        Map<String, List<ReportEntity>> map = list.stream().collect(Collectors.groupingBy(ReportEntity::getCategory));
        List<ReportSelectVO> listVO = new ArrayList<>();
        for (DictionaryDataEntity entity : dictionList) {
            List<ReportEntity> entityList = map.get(entity.getId()) != null ? map.get(entity.getId()) : new ArrayList<>();
            if (CollectionUtils.isNotEmpty(entityList)) {
                ReportSelectVO vo = new ReportSelectVO();
                vo.setId(entity.getId());
                vo.setFullName(entity.getFullName());
                vo.setHasChildren(true);
                vo.setChildren(JsonUtil.getJsonToList(entityList, ReportSelectVO.class));
                listVO.add(vo);
            }
        }
        ListVO vo = new ListVO<>();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    @Operation(summary = "报表发布菜单")
    @Parameters({
            @Parameter(name = "id", description = "模板id", required = true)
    })
    @PostMapping("/{id}/Actions/Module")
    public ActionResult module(@PathVariable String id, @RequestBody MenuModel model) {
        ReportEntity entity = reportService.getById(id);
        if (entity == null) {
            return ActionResult.fail(MsgCode.FA012.get());
        }
        model.setId(id);
        model.setFullName(entity.getFullName());
        model.setEnCode(entity.getEnCode());
        model.setType(10);
        entity.setPlatformRelease(model.getPlatformRelease());
        String json = ReportUtil.http(ApiConst.SAVE_MENU, Method.POST, JsonUtil.entityToMap(model));
        ActionResult result = JsonUtil.getJsonToBean(json, ActionResult.class);
        if (result == null) {
            return ActionResult.fail(MsgCode.FA101.get());
        }
        if (!Objects.equals(result.getCode(), 200)) {
            return ActionResult.fail(result.getMsg());
        }
        reportService.update(id, entity);
        return ActionResult.success(MsgCode.SU011.get());
    }

    @Operation(summary = "获取报表发布菜单")
    @Parameters({
            @Parameter(name = "id", description = "模板id", required = true)
    })
    @GetMapping("/{id}/getReleaseMenu")
    public ActionResult getReleaseMenu(@PathVariable String id) {
        ReportEntity entity = reportService.getById(id);
        if (entity == null) {
            return ActionResult.fail(MsgCode.FA012.get());
        }
        MenuModel model = new MenuModel();
        model.setId(id);
        String json = ReportUtil.http(ApiConst.GET_MENU, Method.POST, JsonUtil.entityToMap(model));
        ActionResult result = JsonUtil.getJsonToBean(json, ActionResult.class);
        ModuleNameVO moduleNameVO = new ModuleNameVO();
        if (Objects.equals(result.getCode(), 200)) {
            moduleNameVO = JsonUtil.getJsonToBean(result.getData(), ModuleNameVO.class);
        }
        ReportInfoVO vo = JsonUtil.getJsonToBean(entity, ReportInfoVO.class);
        vo.setAppIsRelease(0);
        vo.setPcIsRelease(0);
        if (moduleNameVO != null) {
            if (StringUtil.isNotEmpty(moduleNameVO.getPcNames())) {
                vo.setPcIsRelease(1);
                vo.setPcReleaseName(moduleNameVO.getPcNames());
            }
            if (StringUtil.isNotEmpty(moduleNameVO.getAppNames())) {
                vo.setAppIsRelease(1);
                vo.setAppReleaseName(moduleNameVO.getAppNames());
            }
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "获取批量导出数据")
    @Parameters({
            @Parameter(name = "systemId", description = "系统id", required = true),
    })
    @GetMapping("/getExportList")
    public ActionResult getExportList(@RequestParam String systemId) {
        List<ReportEntity> sysList = reportService.getListBySystemId(systemId);
        List<ReportInfoVO> voList = new ArrayList<>();
        for (ReportEntity item : sysList) {
            List<ReportVersionEntity> list = versionService.getList(item.getId(), ReportVersionEntity::getId);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            ReportInfoVO info = reportService.getVersionInfo(list.get(0).getId());
            voList.add(info);
        }
        return ActionResult.success(voList);
    }

    @Operation(summary = "批量创建数据")
    @PostMapping(value = "/importCopy")
    public ActionResult importCopy(@RequestBody ImportCopyModel model) throws DataException {
        try {
            if (CollectionUtils.isNotEmpty(model.getList())) {
                for (ReportInfoVO item : model.getList()) {
                    item.setId(RandomUtil.uuId());
                    item.setEnCode(codeNumService.getCodeFunction(() ->
                            codeNumService.getCodeOnce(CodeConst.BB), encode -> reportService.isEncodeExist(encode)));
                    item.setSystemId(model.getSystemId());
                    String str = reportService.importData(item, 0, false);
                    if (StringUtil.isNotEmpty(str)) {
                        return ActionResult.fail(str);
                    }
                }
            }
            return ActionResult.success(MsgCode.IMP001.get());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return ActionResult.fail(MsgCode.IMP004.get());
    }

    @Operation(summary = "批量删除数据")
    @Parameters({
            @Parameter(name = "systemId", description = "系统id", required = true),
    })
    @GetMapping("/deleteBySystemId")
    public ActionResult deleteBySystemId(@RequestParam String systemId) {
        List<ReportEntity> sysList = reportService.getListBySystemId(systemId);
        for (ReportEntity item : sysList) {
            versionService.removeByTemplateId(item.getId());
            reportService.delete(item.getId());
        }
        return ActionResult.success();
    }
}
