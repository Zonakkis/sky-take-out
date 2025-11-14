package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    /**
     * 根据 id 查询用户
     *
     * @param id
     * @return
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据 openId 查询用户
     *
     * @param openId
     * @return
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time" +
            " from user where openid = #{openId}")
    User getByOpenId(String openId);

    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time)" +
            " values (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
