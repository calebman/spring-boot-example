package com.spring.demo.springbootexample.controller;


import com.spring.demo.springbootexample.beans.Product;
import com.spring.demo.springbootexample.config.Config;
import com.spring.demo.springbootexample.result.ERRORDetail;
import com.spring.demo.springbootexample.result.Pagination;
import com.spring.demo.springbootexample.result.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/v1/product")
@Api(value = "DocController", tags = {"restful api示例"})
public class DocController {

    @Autowired
    Config config;

    static Map<Integer, Product> products = new ConcurrentHashMap<Integer, Product>();

    Logger logger = LoggerFactory.getLogger(DocController.class);

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加产品", httpMethod = "POST", produces = "application/json")
    public WebResult add(@ModelAttribute Product product) {
        logger.debug("添加产品接收信息=>{}", product);
        if (product == null) {
            logger.debug("产品信息不能为空");
            return WebResult.error(ERRORDetail.RC_0101001);
        }
        if (product.getName() == null || "".equals(product.getName())) {
            logger.debug("产品名称不能为空");
            return WebResult.error(ERRORDetail.RC_0101002);
        }
        products.put(products.size(), product);
        return WebResult.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "移除指定产品", httpMethod = "DELETE", produces = "application/json")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "产品ID", required = true, paramType = "path")})
    public WebResult delete(@PathVariable("id") Integer id) {
        logger.debug("移除指定产品接收产品id=>%d", id);
        if (id == null || "".equals(id)) {
            logger.debug("产品id不能为空");
            return WebResult.error(ERRORDetail.RC_0101001);
        }
        products.remove(id);
        return WebResult.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "修改指定产品", httpMethod = "PUT", produces = "application/json")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "产品ID", required = true, paramType = "path")})
    public WebResult update(@PathVariable("id") Integer id, @ModelAttribute Product product) {
        logger.debug("修改指定产品接收产品id与产品信息=>%d,{}", id, product);
        if (id == null || "".equals(id)) {
            logger.debug("产品id不能为空");
            return WebResult.error(ERRORDetail.RC_0101001);
        }
        products.put(id, product);
        return WebResult.success();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取以及筛选产品", httpMethod = "GET", produces = "application/json")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "根据产品名称筛选", required = false, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "根据产品类型筛选", required = false, paramType = "query", dataType = "int")})
    public WebResult searchAll(@ModelAttribute Pagination pagination, String name, String type) {
        logger.debug("获取以及筛选产品接收分页、产品名称、产品类型=>{},{},{}", pagination, name, type);
        if (!pagination.isRequire()) {
            logger.debug("分页参数不能为空");
            return WebResult.error(ERRORDetail.RC_0101003);
        }
        return WebResult.success(products);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取指定产品", httpMethod = "GET", produces = "application/json")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "产品ID", required = true, paramType = "path")})
    public WebResult search(@PathVariable("id") Integer id) {
        logger.debug("获取指定产品接收产品id=>%d", id);
        if (id == null || "".equals(id)) {
            logger.debug("产品id不能为空");
            return WebResult.error(ERRORDetail.RC_0101001);
        }
        return WebResult.success(products.get(id));
    }

    @RequestMapping("/logger")
    @ResponseBody
    public WebResult logger() {
        logger.trace("日志输出 {},{}", "trace", config.getMidpHost());
        logger.debug("日志输出 {},{}", "debug", config.getMidpHost());
        logger.info("日志输出 {},{}", "info", config.getMidpHost());
        logger.warn("日志输出 {},{}", "warn", config.getMidpHost());
        logger.error("日志输出 {},{}", "error", config.getMidpHost());
        return WebResult.success();
    }
}
