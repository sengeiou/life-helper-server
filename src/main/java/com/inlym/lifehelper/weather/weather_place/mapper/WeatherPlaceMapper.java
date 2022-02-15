package com.inlym.lifehelper.weather.weather_place.mapper;

import com.inlym.lifehelper.weather.weather_place.entity.WeatherPlace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author inlym
 * @date 2022-02-13 19:03
 **/
@Mapper
public interface WeatherPlaceMapper {
    /**
     * 插入一条天气地点
     *
     * @param weatherPlace 天气地点
     */
    void insert(@Param("place") WeatherPlace weatherPlace);

    /**
     * 获取指定用户所属的天气地点列表
     *
     * @param userId 用户 ID
     *
     * @return 天气地点列表
     */
    List<WeatherPlace> list(int userId);

    /**
     * 删除一条天气地点
     *
     * @param userId 用户 ID
     * @param id     主键 ID
     */
    void delete(@Param("userId") int userId, @Param("id") int id);
}
