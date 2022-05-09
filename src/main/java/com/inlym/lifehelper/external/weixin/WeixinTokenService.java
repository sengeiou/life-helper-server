package com.inlym.lifehelper.external.weixin;

import com.inlym.lifehelper.external.weixin.pojo.WeixinGetAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 微信服务端接口调用凭据服务
 *
 * <h2>说明
 * <p>围绕着微信服务端接口调用凭据的方法较多，因此将它们剥离出来，单独弄一个类。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022/4/23
 * @since 1.1.2
 **/
@Service
@Slf4j
public class WeixinTokenService {
    /** 在 Redis 中存储微信服务端接口调用凭证的键名 */
    public static final String WEIXIN_ACCESS_TOKEN_KEY = "weixin:token";

    private final WeixinHttpService weixinHttpService;

    private final StringRedisTemplate stringRedisTemplate;

    public WeixinTokenService(WeixinHttpService weixinHttpService, StringRedisTemplate stringRedisTemplate) {
        this.weixinHttpService = weixinHttpService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 从 Redis 中读取凭证
     *
     * @since 1.1.2
     */
    private String getTokenFromRedis() {
        return stringRedisTemplate
            .opsForValue()
            .get(WEIXIN_ACCESS_TOKEN_KEY);
    }

    /**
     * 用于项目内调用获取微信服务端接口调用凭证
     *
     * @since 1.1.2
     */
    public String getToken() {
        String token = getTokenFromRedis();
        return token == null ? refreshTokenInRedis() : token;
    }

    /**
     * 刷新存储在 Redis 中的凭证
     *
     * @since 1.1.2
     */
    public String refreshTokenInRedis() {
        WeixinGetAccessTokenResponse data = weixinHttpService.getAccessToken();
        String token = data.getAccessToken();
        int expiration = data.getExpiresIn();

        stringRedisTemplate
            .opsForValue()
            .set(WEIXIN_ACCESS_TOKEN_KEY, token, Duration.ofSeconds(expiration));

        return token;
    }

    /**
     * 检查凭证是否有效
     *
     * @since 1.1.2
     */
    private boolean checkTokenValid() {
        String token = getTokenFromRedis();
        if (token != null && !token.isEmpty()) {
            try {
                // 说明：
                // 这里调用这个方法只是测试 token 是否能用，调其他方法也是可以的。
                weixinHttpService.getUnlimitedWxacode(token, "pages/index/index", "test", 430);

                // 没报错说明 token 有效
                return true;
            } catch (Exception e) {
                log.error("微信服务端接口调用凭证无效，将重新获取", e);
                return false;
            }
        }

        return false;
    }

    /**
     * 执行更新凭证定时任务
     */
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void execRefreshTokenTimedTask() {
        Long ttl = stringRedisTemplate.getExpire(WEIXIN_ACCESS_TOKEN_KEY, TimeUnit.SECONDS);

        assert ttl != null;
        long minTtl = 1000;

        if (ttl > minTtl && checkTokenValid()) {
            log.debug("[定时任务] 微信服务端接口调用凭证有效，不需要更新");
        } else {
            refreshTokenInRedis();
        }
    }
}