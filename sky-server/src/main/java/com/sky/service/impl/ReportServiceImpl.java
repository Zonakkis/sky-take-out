package com.sky.service.impl;

import com.sky.constant.OrderConstant;
import com.sky.dto.DateCountDTO;
import com.sky.dto.DateSumDTO;
import com.sky.dto.SalesCountDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

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
     *
     * @param begin
     * @param end
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
        List<DateCountDTO> newUserCounts = userMapper.countNewUserByDateBetween(beginDateTime, endDateTime);
        Map<LocalDate, Integer> newUserMap = newUserCounts.stream().collect(
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
        String newUserListString = StringUtils.join(newUserList, ",");
        String totalUserListString = StringUtils.join(totalUserList, ",");

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(dateListString);
        userReportVO.setNewUserList(newUserListString);
        userReportVO.setTotalUserList(totalUserListString);
        return userReportVO;
    }

    /**
     * 获取订单统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        int totalOrderCount = 0;
        int totalValidOrderCount = 0;
        List<DateCountDTO> newOrderCounts = orderMapper.countByDate(null, beginDateTime, endDateTime);
        List<DateCountDTO> newValidOrderCounts = orderMapper.countByDate(OrderConstant.Status.COMPLETED, beginDateTime, endDateTime);
        Map<LocalDate, Integer> newOrderMap = newOrderCounts.stream().collect(
                Collectors.toMap(DateCountDTO::getDate, DateCountDTO::getCount));
        Map<LocalDate, Integer> newValidOrderMap = newValidOrderCounts.stream().collect(
                Collectors.toMap(DateCountDTO::getDate, DateCountDTO::getCount));

        while (!begin.isAfter(end)) {
            dateList.add(begin);
            int orderCount = newOrderMap.getOrDefault(begin, 0);
            int validOrderCount = newValidOrderMap.getOrDefault(begin, 0);
            totalOrderCount += orderCount;
            totalValidOrderCount += validOrderCount;
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
            begin = begin.plusDays(1);
        }

        String dateListString = StringUtils.join(dateList, ",");
        String orderCountListString = StringUtils.join(orderCountList, ",");
        String validOrderCountListString = StringUtils.join(validOrderCountList, ",");
        double completionRate = totalOrderCount == 0 ? 0 : (double) totalValidOrderCount / totalOrderCount;

        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(dateListString);
        orderReportVO.setOrderCountList(orderCountListString);
        orderReportVO.setValidOrderCountList(validOrderCountListString);
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(totalValidOrderCount);
        orderReportVO.setOrderCompletionRate(completionRate);

        return orderReportVO;
    }

    /**
     * 获取销售前十统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);

        List<SalesCountDTO> salesCountDTOS = orderDetailMapper.countSalesTop10Between(beginDateTime, endDateTime);

        String nameListString = salesCountDTOS.stream()
                .map(SalesCountDTO::getName)
                .collect(Collectors.joining(","));
        String numberListString = salesCountDTOS.stream()
                .map(dto -> dto.getCount().toString())
                .collect(Collectors.joining(","));

        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(nameListString);
        salesTop10ReportVO.setNumberList(numberListString);
        return salesTop10ReportVO;
    }

    /**
     * 导出营业数据报表
     *
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate beginDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);


        LocalDateTime begin = LocalDateTime.of(beginDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);

        InputStream input = getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        if (input == null) {
            return;
        }

        try (XSSFWorkbook excel = new XSSFWorkbook(input)) {
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue("时间：" + beginDate + " 至 " + endDate);

            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));


                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            try (ServletOutputStream output = response.getOutputStream()) {
                excel.write(output);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
