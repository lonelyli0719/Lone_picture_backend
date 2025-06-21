package com.lone.lonepicturebackend.manager.factory;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lone.lonepicturebackend.exception.ErrorCode;
import com.lone.lonepicturebackend.exception.ThrowUtils;
import com.lone.lonepicturebackend.manager.upload.PictureUploadTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @description 使用工厂模式创建上传图片模板
 */
@Component
public class UploadFactory {
    @Resource
    private List<PictureUploadTemplate> uploadPictureTemplates;

    private final HashMap<String, PictureUploadTemplate> uploadFactory = new HashMap<>(10);

    @PostConstruct
    private void init() {
        // 初始化方便使用
        uploadPictureTemplates.forEach(uploadTemplate -> {
            Component annotation = uploadTemplate.getClass().getAnnotation(Component.class);
            if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
                uploadFactory.put(annotation.value(), uploadTemplate);
            }
        });
    }

    public PictureUploadTemplate getUploadFactory(String type) {
        PictureUploadTemplate pictureUploadTemplate = uploadFactory.getOrDefault(type, null);
        ThrowUtils.throwIf(pictureUploadTemplate == null, ErrorCode.NOT_FOUND_ERROR, "上传类型不存在");
        return pictureUploadTemplate;
    }
}


