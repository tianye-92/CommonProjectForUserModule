package com.brandslink.cloud.inbound.remote;


import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.inbound.dto.InventoryUpdateDTO;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;

import net.sf.json.JSONObject;

/**
 * 远程调用服务
 */
@FeignClient(name = "brandslink-center-service", fallback = RemoteCenterService.RemoteCenterServiceImpl.class)
public interface RemoteCenterService {

    /**
     * @param sku
     * @description: 根据sku获取详情
     * @return: 返回sku信息集合
     */
    @GetMapping("/sku/skuInfo")
    String getSkuInfoBySku(@RequestParam("sku") String[] sku);

    /**
     * @param warehouseCode：仓库code
     * @return
     * @description: 查询仓库详情
     */
    @GetMapping("/warehouse/detail")
    String getWarehouseByCode(@RequestParam("warehouseCode") String warehouseCode);

    /**
     * @param dictCode: 类型编码
     * @param dataCode: 类型值编码
     * @return
     * @description: 查询码表值详情
     */
    @GetMapping("/dict/data")
    String getDictionaryDataDetail(@RequestParam("dictCode") String dictCode, @RequestParam("dataCode") String dataCode);

    /**
     * @param skuList
     * @return
     * @description: 根据SKU查询仓库库区货位等信息
     */
    @PostMapping("/position/mix")
    String getMixDetailBySku(@RequestBody List<String> skuList);

    /**
     * @param map： warehouseCode:仓库编码，categoryCode：分类编码，sku：商品名称，flag：标志为1，表示同库区同分类
     * @description: 根据仓库编码和分类编码或sku获取库区下货位体积库存等信息
     * @return： 返回货位集合
     */
    @PostMapping("/position/areaStock")
    String getAreaPositionStock(@RequestBody Map<String, String> map);

    /**
     * @param map：warehouseCode:仓库编码，categoryCode：分类编码，sku：商品名称
     * @description: 根据仓库编码和分类编码或sku获取货位体积库存等信息
     * @return： 返回货位集合
     */
    @PostMapping("/position/stock")
    String getPositionStock(@RequestBody Map<String, String> map);

    /**
     * @param warehouseCode
     * @description: 根据仓库编码查询可用库区可用货列可用货位开启且未锁定的货位可用体积信息
     * @return: 返回可用货位集合
     */
    @PostMapping("/position/allAllowVolume")
    String queryAllAllowPosition(@RequestParam("warehouseCode") String warehouseCode);


    /**
     * 通过货位编码查询货位id
     *
     * @param warehouseCode
     * @param positionCode
     * @return
     */
    @PostMapping("/position/info")
    String queryByCodeAndWarehouseCode(@RequestParam("warehouseCode") String warehouseCode, @RequestParam("positionCode") String positionCode);


    /**
     * @param warehouseCode：仓库code
     * @param containerCode：容器编码
     * @return
     * @description: 查询容器信息
     */
    @GetMapping("/container/info")
    String getContainerByCode(@RequestParam("warehouseCode") String warehouseCode, @RequestParam("containerCode") String containerCode);

    /**
     * @return
     * @description: 更新容器工作状态
     */
    @PostMapping("/container/updateState")
    String altWorkState(@RequestBody Map<String, Object> map);

    /**
     * @param inventoryUpdateDTOList
     * @return
     * @description: 到货通知-修改库存
     */
    @PostMapping("/inInventory/arrivalNotice")
    String arrivalNoticeUpdateInventory(@RequestBody List<InventoryUpdateDTO> inventoryUpdateDTOList);


    /**
     * @param param
     * @return
     * @description: QC完成-修改库存
     */
    @PostMapping("/inInventory/qcFinish")
    String qcFinishUpdateInventory(@RequestBody Map<String, Object> param);


    /**
     * @param inventoryUpdateDTO
     * @return
     * @description: 到货完成-修改库存
     */
    @PostMapping("/inInventory/receivingFinish")
    String receivingFinishUpdateInventory(@RequestBody List<InventoryUpdateDTO> inventoryUpdateDTO);


    /**
     * @param sku
     * @return
     * @description: 根据sku查询本地分类
     */
    @GetMapping("/category/getBySku")
    String getCategoryBySku(@RequestParam("sku") String sku);

    /**
     * @param param
     * @return
     * @description: 确认上架-修改库存
     */
    @PostMapping("/inInventory/affirmPutaway")
    String affirmPutawayUpdateInventory(@RequestBody Map<String, Object> param);

    /**
     * @param param
     * @return
     * @description: 异常上报
     */
    @PostMapping("/exInventory/addEx")
    String addEx(@RequestBody Map<String, Object> param);

    /**
     * @param skuList
     * @return
     * @description: 根据sku获取图片路径
     */
    @GetMapping("/sku/skuImg")
    String getSkuPictureBySku(@RequestParam("skuList") List<String> skuList);


    /**
     * 通过货位编码查询货位的库存信息
     *
     * @param param
     * @return
     */
    @PostMapping("/shelfInventory/singleCondition")
    String getShelfInfoBySingleCondition(@RequestBody Map<String, Object> param);


    /**
     * @param dataDto
     * @return
     * @description: 新增仓库数据
     */
    @PostMapping(value = "/inventoryData/insert")
    String insertInventoryData(@RequestBody List<Map<String, Object>> dataDto);

    /**
     * @param name
     * @return
     * @description: 通过仓库名称查仓库code
     */
    @GetMapping("/warehouse/getWarehouseByName")
    String getWarehouseByName(@RequestParam("name") String name);

    
    
    /**
     * 客户系统-入库提交订单
     * @param map
     * @return
     */
    @PostMapping("/inInventory/submitOrders")
    String submitOrdersUpdateInventory(@RequestBody List<Map<String,Object>> map);
    
    
    
    
    
    
    
    
    
    
    /**
     * 断路降级
     */
    @Service
    class RemoteCenterServiceImpl implements RemoteCenterService {
        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "基础服务异常"));
        }

        @Override
        public String getWarehouseByCode(String warehouseCode) {
            return "Remote invoke fail!!";
        }

        @Override
        public String getDictionaryDataDetail(String dictCode, String dataCode) {
            return "Remote invoke fail!!";
        }

        @Override
        public String getMixDetailBySku(List<String> skuList) {
            return "Remote invoke fail!!";
        }

        @Override
        public String getAreaPositionStock(Map<String, String> map) {
            return "getAreaPositionStock Remote invoke fail!!";
        }

        @Override
        public String getPositionStock(Map<String, String> paramMap) {
            return "getPositionStock Remote invoke fail!!";
        }

        @Override
        public String queryAllAllowPosition(String warehouseCode) {
            return "getPositionStock Remote invoke fail!!";
        }

        @Override
        public String getSkuInfoBySku(String[] sku) {
            return "getSkuInfoBySku failed !!!";
        }

        @Override
        public String getContainerByCode(String warehouseCode, String code) {
            return "getContainerByCode Remote invoke failed !!!";
        }

        @Override
        public String altWorkState(Map<String, Object> map) {
            return "altWorkState Remote invoke failed !!!";
        }

        @Override
        public String getCategoryBySku(String sku) {
            return null;
        }

        @Override
        public String qcFinishUpdateInventory(Map<String, Object> param) {
            return "qcFinishUpdateInventory Remote invoke failed !!!";
        }

        @Override
        public String affirmPutawayUpdateInventory(Map<String, Object> param) {
            return "affirmPutawayUpdateInventory Remote invoke failed !!!";
        }

        @Override
        public String addEx(Map<String, Object> param) {
            return "addEx Remote invoke failed !!!";
        }

        @Override
        public String arrivalNoticeUpdateInventory(List<InventoryUpdateDTO> inventoryUpdateDTOList) {
            return "arrivalNoticeUpdateInventory Remote  failed !!!";
        }

        @Override
        public String receivingFinishUpdateInventory(List<InventoryUpdateDTO> inventoryUpdateDTO) {
            return "receivingFinishUpdateInventory Remote failed !!!";
        }

        @Override
        public String queryByCodeAndWarehouseCode(String warehouseCode, String positionCode) {
            return "queryByCodeAndWarehouseCode Remote failed !!!";
        }

        @Override
        public String getSkuPictureBySku(List<String> skuList) {
            return "getSkuPictureBySku Remote failed !!!";
        }

        @Override
        public String getShelfInfoBySingleCondition(Map<String, Object> param) {
            return "getShelfInfoBySingleCondition Remote failed !!!";
        }

        @Override
        public String insertInventoryData(List<Map<String, Object>> dataDto) {
            return "insertInventoryData Remote failed !!!";
        }

        @Override
        public String getWarehouseByName(String name) {
            return null;
        }

		@Override
		public String submitOrdersUpdateInventory(List<Map<String, Object>> map) {
			return "submitOrdersUpdateInventory Remote failed !!!";
		}
    }
}





