package com.sky.mapper;

import com.sky.dto.DateCountDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {


    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time)" +
            " values (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);


    @Select("select count(*) as count, date(create_time) as date " +
            "from user " +
            "where create_time between #{begin} and #{end} " +
            "group by date(create_time)")
    List<DateCountDTO> countNewUserByDateBetween(LocalDateTime begin, LocalDateTime end);


    /**
    * 指定时间前的用户数
    *
    * @param dateTime
    */
    @Select("select count(*) " +
            "from user " +
            "where create_time < #{dateTime}")
    int countBefore(LocalDateTime dateTime);

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
}
