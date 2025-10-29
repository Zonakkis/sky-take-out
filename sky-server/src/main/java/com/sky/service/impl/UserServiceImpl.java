package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.UrlConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.WXLoginDTO;
import com.sky.entity.User;
import com.sky.enumeration.JwtType;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.JwtService;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    public UserLoginVO wxLogin(UserLoginDTO userLoginDTO) {
        // 1. 调用微信登录接口，获取 openId
        String openId = getOpenId(userLoginDTO.getCode());

        // 2. 判断 openId 是否获取成功
        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 3. 判断用户是否注册，未注册则自动注册
        User user = userMapper.getByOpenId(openId);

        // 用户未注册，自动注册
        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        // 4. 生成Jwt Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = jwtService.generate(JwtType.USER, claims);
        // 5. 返回结果
        return UserLoginVO.builder()
                .id(user.getId())
                .openid(openId)
                .token(token)
                .build();

    }

    private String getOpenId(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(UrlConstant.WX_LOGIN, params);
        WXLoginDTO wxLoginDTO = JSON.parseObject(json, WXLoginDTO.class);
        return wxLoginDTO.getOpenId();
    }
}
