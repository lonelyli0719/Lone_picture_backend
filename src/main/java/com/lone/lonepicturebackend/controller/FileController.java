package com.lone.lonepicturebackend.controller;

import com.lone.lonepicturebackend.annotation.AuthCheck;
import com.lone.lonepicturebackend.common.BaseResponse;
import com.lone.lonepicturebackend.common.ResultUtils;
import com.lone.lonepicturebackend.exception.BusinessException;
import com.lone.lonepicturebackend.exception.ErrorCode;
import com.lone.lonepicturebackend.manager.CosManager;
import com.lone.lonepicturebackend.model.constant.UserConstant;
import com.lone.lonepicturebackend.service.PictureService;
import com.lone.lonepicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * @description 文件上传接口
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosManager cosManager;

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    /**
     * 测试文件上传
     *
     * @param multipartFile 上传的文件
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
}




