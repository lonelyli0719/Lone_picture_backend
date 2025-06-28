package com.lone.lonepicturebackend.api.imagesearch;

import com.lone.lonepicturebackend.api.imagesearch.model.ImageSearchResult;
import com.lone.lonepicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.lone.lonepicturebackend.api.imagesearch.sub.GetImageListApi;
import com.lone.lonepicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {
    // 最大重试次数
    private static final int MAX_RETRIES = 5;
    // 支持的图片格式（按优先级排序）
    private static final String[] FORMATS = {"jpg", "png", "jpeg"};

    /**
     * webp格式百度没办法搜索的办法二
     */
//    public static List<ImageSearchResult> searchImage(String originalUrl) {
//        List<ImageSearchResult> result;
//
//        // 1. 转换成其他格式
//        for (String format : FORMATS) {
//            String convertedUrl = convertImageFormat(originalUrl, format);
//            result = tryWithRetries(convertedUrl);
//            if (isValidResult(result)) {
//                return result;
//            }
//        }
//
//        // 2. 全部失败后抛出异常
//        throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片搜索失败，已尝试所有格式且重试 " + MAX_RETRIES + " 次");
//    }

    /**
     * 对指定 URL 进行多次重试（最多 MAX_RETRIES 次）
     */
    private static List<ImageSearchResult> tryWithRetries(String url) {
        int retryCount = 0;
        List<ImageSearchResult> result = null;

        while (retryCount < MAX_RETRIES) {
            try {
                result = doSearchImage(url);
                if (isValidResult(result)) {
                    // 成功则立即返回
                    return result;
                }
            } catch (Exception e) {
                log.warn("第 {} 次尝试失败 (URL: {}): {}", retryCount + 1, url, e.getMessage());
            }

            retryCount++;
        }
        // 全部失败返回 null
        return null;
    }

    /**
     * 本来的代码（鱼皮写的）
     */
    private static List<ImageSearchResult> doSearchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    /**
     * 转换图片格式（替换后缀）
     */
    private static String convertImageFormat(String originalUrl, String newFormat) {
        int lastDotIndex = originalUrl.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return originalUrl + "." + newFormat;
        }
        return originalUrl.substring(0, lastDotIndex) + "." + newFormat;
    }

    /**
     * 检查结果是否有效
     */
    private static boolean isValidResult(List<ImageSearchResult> resultList) {
        return resultList != null && !resultList.isEmpty();
    }


    /**
     * 搜索图片，用门面模式整合以图搜图全过程
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}
