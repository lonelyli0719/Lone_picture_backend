package com.lone.lonepicturebackend.model.enums;

import com.lone.lonepicturebackend.exception.BusinessException;
import com.lone.lonepicturebackend.exception.ErrorCode;
import com.lone.lonepicturebackend.exception.ThrowUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 图片审核枚举
 */
@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    private final String text;

    private final int value;

    //使用静态Map初始化一次开销更低
    private static final Map<Integer, PictureReviewStatusEnum> PICTURE_REVIEW_STATUS_ENUM_MAP =
            Arrays.stream(PictureReviewStatusEnum.values())
                    .collect(Collectors.toMap(PictureReviewStatusEnum::getValue, e -> e));

    PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value) {
        PictureReviewStatusEnum pictureReviewStatusEnum = value == null ? null : PICTURE_REVIEW_STATUS_ENUM_MAP.getOrDefault(value, null);
        ThrowUtils.throwIf(Objects.isNull(pictureReviewStatusEnum), new BusinessException(ErrorCode.PARAMS_ERROR));
        return pictureReviewStatusEnum;
    }
}

