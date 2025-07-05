package com.lone.lonepicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lone.lonepicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.lone.lonepicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.lone.lonepicturebackend.model.entity.SpaceUser;
import com.lone.lonepicturebackend.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-07-03 22:15:00
*/
public interface SpaceUserService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    void validSpaceUser(SpaceUser spaceUser, boolean add);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

}
