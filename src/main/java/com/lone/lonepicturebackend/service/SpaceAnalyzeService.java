package com.lone.lonepicturebackend.service;

import com.lone.lonepicturebackend.model.dto.space.analyze.*;
import com.lone.lonepicturebackend.model.entity.Space;
import com.lone.lonepicturebackend.model.entity.User;
import com.lone.lonepicturebackend.model.vo.space.analyze.*;

import java.util.List;

public interface SpaceAnalyzeService{

    /**
     * 获取空间使用分析数据
     *
     * @param spaceUsageAnalyzeRequest SpaceUsageAnalyzeRequest 请求参数
     * @param loginUser                当前登录用户
     * @return SpaceUsageAnalyzeResponse 分析结果
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取空间分类使用分析数据
     *
     * @param spaceCategoryAnalyzeRequest SpaceCategoryAnalyzeRequest 请求参数
     * @param loginUser                    当前登录用户
     * @return List<SpaceCategoryAnalyzeResponse> 分析结果
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    /**
     * 获取空间标签使用分析数据
     *
     * @param spaceTagAnalyzeRequest SpaceTagAnalyzeRequest 请求参数
     * @param loginUser               当前登录用户
     * @return List<SpaceTagAnalyzeResponse> 分析结果
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 获取空间大小使用分析数据
     *
     * @param spaceSizeAnalyzeRequest SpaceSizeAnalyzeRequest 请求参数
     * @param loginUser                当前登录用户
     * @return List<SpaceSizeAnalyzeResponse> 分析结果
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 获取空间用户使用分析数据
     *
     * @param spaceUserAnalyzeRequest SpaceUserAnalyzeRequest 请求参数
     * @param loginUser               当前登录用户
     * @return List<SpaceUserAnalyzeResponse> 分析结果
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 获取空间排行分析数据
     *
     * @param spaceRankAnalyzeRequest SpaceRankAnalyzeRequest 请求参数
     * @param loginUser               当前登录用户
     * @return List<Space> 排行数据
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
