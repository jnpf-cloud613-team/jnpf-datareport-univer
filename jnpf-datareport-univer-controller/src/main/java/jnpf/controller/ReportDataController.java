package jnpf.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jnpf.base.ActionResult;
import jnpf.constant.FileTypeConstant;
import jnpf.entity.FileParameter;
import jnpf.enums.ImageEnum;
import jnpf.model.report.ReportPagination;
import jnpf.model.report.UploaderVO;
import jnpf.service.ReportVersionService;
import jnpf.univer.model.UniverPreview;
import jnpf.util.*;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author ：JNPF开发平台组
 * @version: V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date ：2024/5/11 下午4:35
 */
@Tag(name = "报表数据", description = "Report")
@RestController
@RequestMapping("/api/Report/data")
public class ReportDataController {

    @Autowired
    private ReportVersionService reportVersionService;

    @Operation(summary = "预览")
    @Parameters({
            @Parameter(name = "id", description = "模板id")
    })
    @PostMapping("/{id}/preview")
    public ActionResult preview(@PathVariable String id, @RequestBody Map<String, Object> params) {
        ReportPagination pagination = JsonUtil.getJsonToBean(params, ReportPagination.class);
        UniverPreview preview = reportVersionService.preview(id, pagination, params);
        return ActionResult.success(preview);
    }

    @Operation(summary = "预览")
    @Parameters({
            @Parameter(name = "id", description = "模板id")
    })
    @PostMapping("/{id}/previewTemplate")
    public ActionResult previewTemplate(@PathVariable String id, @RequestBody Map<String, Object> params) {
        ReportPagination pagination = JsonUtil.getJsonToBean(params, ReportPagination.class);
        UniverPreview preview = reportVersionService.previewTemplate(id, pagination, params);
        return ActionResult.success(preview);
    }

    @Operation(summary = "上传图片")
    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult upload(@RequestPart("file") MultipartFile file) {
        UploaderVO vo = new UploaderVO();
        try {
            String fileName = RandomUtil.uuId() + ".jpeg";
            String url = "/api/Report/data/Download?name=" + fileName + "&encryption=";
            FileInfo fileInfo = FileUploadUtils.uploadFile(new FileParameter(FileTypeConstant.ANNEX, fileName), file);
            vo.setName(fileInfo.getFilename());
            vo.setUrl(UploaderUtil.uploaderFile(url, fileInfo.getFilename() + "#" + FileTypeConstant.ANNEX));
        } catch (Exception e) {
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "远端接口下载图片")
    @PostMapping(value = "/downImg")
    public ActionResult upload(@RequestBody ReportPagination pagination) {
        UploaderVO vo = new UploaderVO();
        try {
            byte[] bytes = null;
            String imgValue = pagination.getImgValue();
            String imgType = pagination.getImgType();
            if (StringUtil.isNotEmpty(imgValue)) {
                if (Objects.equals(ImageEnum.BASE64.name(), imgType)) {
                    String regex = "data:image/\\w+;base64,";
                    String base64Img = imgValue;
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(imgValue);
                    if (matcher.find()) {
                        base64Img = imgValue.replace(matcher.group(),"");
                    }
                    bytes = Base64.decode(base64Img);
                } else {
                    HttpRequest request = HttpRequest.of(imgValue).method(Method.GET);
                    bytes = request.execute().bodyBytes();
                }
            }
            if (bytes != null && bytes.length > 0) {
                String fileName = RandomUtil.uuId() + ".jpeg";
                String url = "/api/Report/data/Download?name=" + fileName + "&encryption=";
                FileInfo fileInfo = FileUploadUtils.uploadFile(new FileParameter(FileTypeConstant.ANNEX, fileName), bytes);
                vo.setName(fileInfo.getFilename());
                vo.setUrl(UploaderUtil.uploaderFile(url, fileInfo.getFilename() + "#" + FileTypeConstant.ANNEX));
            }
        } catch (Exception e) {
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "上传excel")
    @PostMapping(value = "/ImportExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult importExcel(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        UniverPreview preview = reportVersionService.importExcel(multipartFile);
        return ActionResult.success(preview);
    }

    @Operation(summary = "下载excel")
    @Parameters({
            @Parameter(name = "id", description = "模板id")
    })
    @PostMapping("/{id}/DownExcel")
    public ActionResult down(@PathVariable String id, @RequestBody Map<String, Object> params) {
        ReportPagination pagination = JsonUtil.getJsonToBean(params, ReportPagination.class);
        pagination.setId(id);
        UploaderVO vo = reportVersionService.downExcel(pagination);
        return ActionResult.success(vo);
    }

    @NoDataSourceBind()
    @Operation(summary = "下载文件")
    @GetMapping("/Download")
    public void downExcel(String encryption, String name) {
        String fileNameAll = DesUtil.aesDecode(encryption).replaceAll("\n", "");
        if (!StringUtil.isEmpty(fileNameAll)) {
            String[] data = fileNameAll.split("#");
            String fileName = data.length > 1 ? data[1] : "";
            String type = data.length > 2 ? data[2] : "";
            String typePath = FilePathUtil.getFilePath(type.toLowerCase());
            FileUploadUtils.downloadFile(new FileParameter(typePath, fileName), inputStream -> {
                if (fileName.endsWith(".jpeg")) {
                    FileDownloadUtil.flushFile(inputStream, name);
                } else {
                    FileDownloadUtil.outFile(inputStream, name);
                }

            });
        }
    }

}
