package com.sky.service.impl;

import com.sky.dto.DateCountDTO;
import com.sky.dto.DateSumDTO;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    public ReportServiceImpl(OrderMapper orderMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    /**
     * 获取营业额统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        List<LocalDate> dateList = new ArrayList<>();
        List<BigDecimal> turnoverList = new ArrayList<>();

        List<DateSumDTO> dateSumList = orderMapper.sumByDateBetween(beginDateTime, endDateTime);
        Map<LocalDate, BigDecimal> turnoverMap = dateSumList.stream().collect(
                Collectors.toMap(DateSumDTO::getDate, DateSumDTO::getTotal));

        while (!begin.isAfter(end)) {
            dateList.add(begin);
            // 没有当日营业额则为0
            BigDecimal turnover = turnoverMap.getOrDefault(begin, BigDecimal.ZERO);
            turnoverList.add(turnover);
            begin = begin.plusDays(1);
        }

        String dateListString = StringUtils.join(dateList, ",");
        String turnoverListString = StringUtils.join(turnoverList, ",");

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(dateListString);
        turnoverReportVO.setTurnoverList(turnoverListString);

        return turnoverReportVO;
    }


    /**
     * 获取用户统计数据
     * @param begin
     * @param end
     *
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        // 起始日期前的用户数量
        int totalUser = userMapper.countBefore(beginDateTime);
        // 有新增用户数的日期列表
        List<DateCountDTO> dateCountList = userMapper.countNewUserByDateBetween(beginDateTime, endDateTime);
        Map<LocalDate,Integer> newUserMap = dateCountList.stream().collect(
                Collectors.toMap(DateCountDTO::getDate, DateCountDTO::getCount));

        while (!begin.isAfter(end)) {
            dateList.add(begin);
            // 没有新增用户数则为0
            int newUser = newUserMap.getOrDefault(begin, 0);
            // 总用户数 = 当日前的总用户数 + 当日新增用户数
            totalUser += newUser;
            newUserList.add(newUser);
            totalUserList.add(totalUser);
            begin = begin.plusDays(1);
        }

        String dateListString = StringUtils.join(dateList, ",");
        String newUserListString =  StringUtils.join(newUserList, ",");
        String totalUserListString = StringUtils.join(totalUserList, ",");

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(dateListString);
        userReportVO.setNewUserList(newUserListString);
        userReportVO.setTotalUserList(totalUserListString);
        return userReportVO;
    }
}
