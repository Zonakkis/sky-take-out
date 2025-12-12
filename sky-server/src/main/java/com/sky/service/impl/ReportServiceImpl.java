package com.sky.service.impl;

import com.sky.dto.DateSumDTO;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
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

    private final OrderMapper orderMapper;

    public ReportServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
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
            turnoverList.add(turnoverMap.getOrDefault(begin, BigDecimal.ZERO));
            begin = begin.plusDays(1);
        }

        String dateListString = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String turnoverListString = turnoverList.stream()
                .map(BigDecimal::toString)
                .collect(Collectors.joining(","));

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(dateListString);
        turnoverReportVO.setTurnoverList(turnoverListString);

        return turnoverReportVO;
    }
}
