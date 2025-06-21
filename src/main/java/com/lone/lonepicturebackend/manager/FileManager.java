package com.lone.lonepicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.lone.lonepicturebackend.config.CosClientConfig;
import com.lone.lonepicturebackend.exception.BusinessException;
import com.lone.lonepicturebackend.exception.ErrorCode;
import com.lone.lonepicturebackend.exception.ThrowUtils;
import com.lone.lonepicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件上传服务
 * @deprecated 已经废弃，改用upload包的模板方法
 */
@Service
@Slf4j
@Deprecated
public class FileManager {
    /**
     * 1 兆
     */
    private static final long ONE_M = 1024 * 1024L;

    private static final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;


    /**
     * 上传图片
     * @param multipartFile 文件
     * @param uploadPathPrefix 上传路径前缀
     * @return 图片信息
     */
    public UploadPictureResult uploadPicture2(MultipartFile multipartFile, String uploadPathPrefix) {
        validPicture(multipartFile);
        // 图片上传地址
        String imagePath = generateImageUploadPath(multipartFile, uploadPathPrefix);
        try {
            File uploadFile = File.createTempFile(imagePath, null);
            multipartFile.transferTo(uploadFile);
            return analyzeCosReturn(new AnalyzeCosParams(cosManager.putPictureObject(imagePath, uploadFile), FileUtil.mainName(multipartFile.getOriginalFilename()), imagePath));
        } catch (Exception e) {
            log.error("FileManager#uploadPicture2 error {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传图片失败");
        } finally {
            try {
                FileUtil.del(imagePath);
            } catch (IORuntimeException e) {
                log.error("FileManager#uploadPicture2 del filePath {}, error {}", imagePath, e.getMessage());
            }
        }
    }

    private UploadPictureResult analyzeCosReturn(AnalyzeCosParams analyzeCosParams) {
        ImageInfo imageInfo = analyzeCosParams.getPutObjectResult().getCiUploadResult().getOriginalInfo().getImageInfo();
        return UploadPictureResult.builder()
                .picFormat(imageInfo.getFormat())
                .picHeight(imageInfo.getHeight())
                .picWidth(imageInfo.getHeight())
                .picSize((long) imageInfo.getQuality())
                .picScale(NumberUtil.round(imageInfo.getHeight() * 1.0 / imageInfo.getWidth(), 2).doubleValue())
                .picName(analyzeCosParams.getImageName())
                .url(String.format("%s/%s", cosClientConfig.getHost(), analyzeCosParams.getImagePath()))
                .build();

    }

    private String generateImageUploadPath(MultipartFile multipartFile, String uploadPathPrefix) {
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadPath = String.format("%s_%s.%s", LocalDate.now(), RandomUtil.randomString(16), originalFilename);
        return String.format("%s/%s", uploadPathPrefix, uploadPath);
    }

    /**
     * 校验文件
     *
     * @param multipartFile multipart 文件
     */
    public void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

    public UploadPictureResult uploadPictureByUrl(String fileUrl, String uploadPathPrefix) {
        // 校验图片
        // validPicture(multipartFile);
        validUrlPicture(fileUrl);
        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        // String originFilename = multipartFile.getOriginalFilename();
        String originFilename = FileUtil.mainName(fileUrl);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // multipartFile.transferTo(file);
            HttpUtil.downloadFile(fileUrl, file);
            // 上传图片
            return analyzeCosReturn(new AnalyzeCosParams(cosManager.putPictureObject(uploadPath, file), FileUtil.mainName(originFilename), uploadPath));
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 根据url校验文件
     * @param fileUrl
     */
    private void validUrlPicture(String fileUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");
        try {
            // 1. 验证 URL 格式  验证是否是合法的 URL
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }
        // 2. 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");
        // 3. 发送 HEAD 请求以验证文件是否存在
        // 使用 try with resources 简化流程
        try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
            // 未正常返回，无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 4. 校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 5. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowUtils.throwIf(contentLength > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        }
    }






}

/**
 * 不用成员变量因为多线程时会出问题
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class AnalyzeCosParams {
    private PutObjectResult putObjectResult;
    private String imageName;
    private String imagePath;
}
