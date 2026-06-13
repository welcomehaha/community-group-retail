package com.community.controller.admin;

import com.community.result.Result;
import com.community.service.ReportService;
import com.community.vo.OrderReportVO;
import com.community.vo.SalesTop10ReportVO;
import com.community.vo.TurnoverReportVO;
import com.community.vo.UserReportVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 交易分析相关接口
 */
@RestController
@RequestMapping("/admin/report")
@Tag(name = "交易分析相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * GMV统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @Operation(summary = "GMV统计")
    @PreAuthorize("@permissionService.hasAuthority('report:statistics:turnover')")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("GMV数据统计：{},{}",begin,end);
        return Result.success(reportService.getTurnoverStatistics(begin,end));
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @Operation(summary = "用户统计")
    @PreAuthorize("@permissionService.hasAuthority('report:statistics:user')")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户数据统计：{},{}",begin,end);
        return Result.success(reportService.getUserStatistics(begin,end));
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/orderStatistics")
    @Operation(summary = "订单统计")
    @PreAuthorize("@permissionService.hasAuthority('report:statistics:order')")
    public Result<OrderReportVO> retailOrderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单数据统计：{},{}",begin,end);
        return Result.success(reportService.getOrderStatistics(begin,end));
    }

    /**
     * 商品销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @Operation(summary = "商品销量排名top10")
    @PreAuthorize("@permissionService.hasAuthority('report:statistics:top10')")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量排名top10：{},{}",begin,end);
        return Result.success(reportService.getSalesTop10(begin,end));
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @GetMapping("/export")
    @Operation(summary = "导出运营数据报表")
    @PreAuthorize("@permissionService.hasAuthority('report:statistics:export')")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }
}


