package com.brandslink.cloud.inbound.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xiaozhang
 * @version 1.0
 * @description: 入库客户端业务层
 * @date 2019/8/27 10:46
 */
public interface InboundClientOrderService {
	
	
	/**
	 * 添加入库订单信息
	 * @param receiveArrivalNotice
	 * @return
	 */
	boolean insertReceiveArrivalNotice(ReceiveArrivalNotice notice) throws SQLIntegrityConstraintViolationException;
	
	/**
	 * 修改入库订单信息
	 * @param receiveArrivalNotice
	 * @return
	 */
	boolean updateReceiveArrivalNotice(ReceiveArrivalNotice notice);
	
	
	/**
	 * 获取入库单号
	 * @return
	 */
	String setSourceId();
	
	
	
	/**
	 * 删除入库订单
	 * @param list
	 */
	void deleteOrders(List<Integer> list);
	
	
	
	/**
	 * 客户端到货通知单信息查询
	 * @param receiveArrivalNotice
	 * @return
	 */
	Page<ReceiveArrivalNotice> findAll(ReceiveArrivalNoticeInfo receiveArrivalNotice);
	
	
	
	/**
	 * 订单确认
	 */
	void affirmOrder(List<String> list);
	
	
    /**
     * @description: 批量导入订单
     * @return
     * @param file
     * @param flag
     */
    String orderImport(MultipartFile file, String flag);

    /**
     * @description: 批量导出订单
     * @param sourceId: 来源单号
     * @param response: 响应对象
     * @return
     */
    String orderExport(String sourceId, HttpServletResponse response);

    /**
     * @description: 下载订单导入模板
     * @param flag
     * @return
     */
    void downloadTemplate(String flag, HttpServletRequest request, HttpServletResponse response);
}
