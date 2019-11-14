package me.mizhoux.dao;

import me.mizhoux.entity.AppFacilitatorInfoEntity;

public interface AppFacilitatorInfoEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AppFacilitatorInfoEntity record);

    int insertSelective(AppFacilitatorInfoEntity record);

    AppFacilitatorInfoEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AppFacilitatorInfoEntity record);

    int updateByPrimaryKey(AppFacilitatorInfoEntity record);
}