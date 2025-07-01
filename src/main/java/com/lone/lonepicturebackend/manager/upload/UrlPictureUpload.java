package com.lone.lonepicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.lone.lonepicturebackend.exception.BusinessException;
import com.lone.lonepicturebackend.exception.ErrorCode;
import com.lone.lonepicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 通过 URL 上传图片
 */
@Component("url")
public class UrlPictureUpload extends PictureUploadTemplate {
    /**
     * 1 兆
     */
    private static final long ONE_M = 1024 * 1024L;

    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
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

    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 从 URL 中提取文件名
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        // 下载文件到临时目录
        HttpUtil.downloadFile(fileUrl, file);
        //以下方法是用apache httpclient下载文件
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpGet httpGet = new HttpGet(fileUrl);
//
//            // 设置浏览器级别的请求头，避免被服务器拒绝
//            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
//            httpGet.setHeader("Accept", "*/*");
//            httpGet.setHeader("Connection", "keep-alive");
//
//            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//            int statusCode = httpResponse.getStatusLine().getStatusCode();
//
//            // 检查HTTP状态码
//            if (statusCode != 200) {
//                throw new RuntimeException("下载失败，HTTP状态码: " + statusCode +
//                        "，URL: " + fileUrl);
//            }
//
//            HttpEntity entity = httpResponse.getEntity();
//            if (entity == null) {
//                throw new RuntimeException("服务器返回空内容，URL: " + fileUrl);
//            }
//
//            // 确保目录存在
//            File parentDir = file.getParentFile();
//            if (!parentDir.exists() && !parentDir.mkdirs()) {
//                throw new RuntimeException("无法创建目录: " + parentDir.getAbsolutePath());
//            }
//
//            // 写入文件
//            try (InputStream inputStream = entity.getContent();
//                 FileOutputStream outputStream = new FileOutputStream(file)) {
//
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//            }
//            // 验证文件是否成功写入
//            if (!file.exists() || file.length() == 0) {
//                throw new RuntimeException("文件写入失败或文件为空: " + file.getAbsolutePath());
//            }
//        }
    }
}

