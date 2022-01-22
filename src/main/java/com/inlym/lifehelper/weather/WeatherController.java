package com.inlym.lifehelper.weather;

import com.inlym.lifehelper.common.constant.CustomRequestAttribute;
import com.inlym.lifehelper.common.validation.LocationString;
import com.inlym.lifehelper.external.hefeng.model.MinutelyRain;
import com.inlym.lifehelper.external.hefeng.model.WeatherNow;
import com.inlym.lifehelper.location.LocationService;
import com.inlym.lifehelper.location.model.LocationCoordinate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 天气数据控制器
 *
 * @author inlym
 * @since 2022-01-21 23:36
 **/
@RestController
@Validated
public class WeatherController {
    private final WeatherService weatherService;

    private final LocationService locationService;

    public WeatherController(WeatherService weatherService, LocationService locationService) {
        this.weatherService = weatherService;
        this.locationService = locationService;
    }

    /**
     * 获取经纬度坐标
     * <p>
     * [主要逻辑]
     * 如果请求传了有效的经纬度字符串则直接使用，否则通过 IP 地址换取经纬度坐标。
     *
     * @param ip       IP 地址
     * @param location 经纬度字符串
     */
    private LocationCoordinate getLocationCoordinate(String ip, String location) {
        if (location != null) {
            return LocationService.parseLocationString(location);
        } else {
            return locationService.getLocationCoordinateByIp(ip);
        }
    }

    /**
     * 获取实时天气
     *
     * @param location `120.12,30.34` 格式的经纬度字符串
     */
    @GetMapping("/weather/now")
    public WeatherNow getWeatherNow(@LocationString @RequestParam(name = "location", required = false) String location, HttpServletRequest request) {
        LocationCoordinate coordinate = getLocationCoordinate((String) request.getAttribute(CustomRequestAttribute.CLIENT_IP), location);

        return weatherService.getWeatherNow(coordinate.getLongitude(), coordinate.getLatitude());
    }

    /**
     * 获取格点实时天气
     *
     * @param location `120.12,30.34` 格式的经纬度字符串
     */
    @GetMapping("/weather/grid-now")
    public WeatherNow getGridWeatherNow(@LocationString @RequestParam(name = "location", required = false) String location, HttpServletRequest request) {
        LocationCoordinate coordinate = getLocationCoordinate((String) request.getAttribute(CustomRequestAttribute.CLIENT_IP), location);

        return weatherService.getGridWeatherNow(coordinate.getLongitude(), coordinate.getLatitude());
    }

    /**
     * 获取分钟级降水
     *
     * @param location `120.12,30.34` 格式的经纬度字符串
     */
    @GetMapping("/weather/rain")
    public MinutelyRain getMinutelyRain(@LocationString @RequestParam(name = "location", required = false) String location, HttpServletRequest request) {
        LocationCoordinate coordinate = getLocationCoordinate((String) request.getAttribute(CustomRequestAttribute.CLIENT_IP), location);

        return weatherService.getMinutelyRain(coordinate.getLongitude(), coordinate.getLatitude());
    }
}