package com.inlym.lifehelper.weather.weather_place.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 微信选择位置结果
 * <p>
 * [数据来源]
 * 微信小程序中调用 wx.chooseLocation 并直接将该结果返回给服务端。
 *
 * @author inlym
 * @date 2022-02-13 20:53
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/api/location/wx.chooseLocation.html">wx.chooseLocation</a>
 **/
@Data
public class WeixinChooseLocationDTO {
    /** 位置名称 */
    @NotEmpty
    private String name;

    /** 详细地址 */
    @NotEmpty
    private String address;

    /** 经度 */
    @NotNull
    private Double longitude;

    /** 纬度 */
    @NotNull
    private Double latitude;
}
