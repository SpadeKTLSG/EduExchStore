package com.shop.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.constant.SystemConstants;
import com.shop.pojo.Result;
import com.shop.pojo.dto.ProdCateDTO;
import com.shop.pojo.dto.ProdLocateDTO;
import com.shop.serve.service.ProdCateService;
import com.shop.serve.service.ProdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制
 *
 * @author SK
 * @date 2024/06/03
 */
@Slf4j
@Tag(name = "Prod", description = "商品")
@RequestMapping("/admin/prod")
@RestController
public class ProdController {

    @Autowired
    private ProdService prodService;

    @Autowired
    private ProdCateService prodCateService;


    //! Func

    /**
     * 管理员审核单件商品 -> name + userId 确定唯一商品
     * Update 状态字段
     * <p>联表修改</p>
     */
    @PutMapping("/check")
    @Operation(summary = "管理员审核单件商品")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result check(@RequestBody ProdLocateDTO prodLocateDTO) {
        prodService.check(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/check


    /**
     * 管理员冻结单件商品 -> name + userId 确定唯一商品
     */
    @PutMapping("/freeze")
    @Operation(summary = "管理员冻结单件商品")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result freeze(@RequestBody ProdLocateDTO prodLocateDTO) {
        prodService.freeze(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/freeze


    /**
     * 管理员分页查看需要审核商品
     * <p>联表分页</p>
     */
    @GetMapping("/page2Check")
    @Operation(summary = "管理员分页查看需要审核商品")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result page2Check(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.page2Check(current));
    }
    //http://localhost:8085/admin/prod/page2Check


    //! ADD

    /**
     * 添加商品分类
     */
    @PostMapping("/cate/save")
    @Operation(summary = "添加商品分类")
    @Parameters(@Parameter(name = "prodCateDTO", description = "商品分类DTO", required = true))
    public Result saveCate(@RequestBody ProdCateDTO prodCateDTO) {
        prodCateService.saveCate(prodCateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/cate/save


    //! DELETE

    /**
     * 管理员删除一件商品 -> name + userId 确定唯一商品
     * <p>联表删除</p>
     */
    @DeleteMapping("/delete/one")
    @Operation(summary = "管理员删除一件商品")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result deleteByNameUser(@RequestBody ProdLocateDTO prodLocateDTO) {
        prodService.deleteByNameUser(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/delete/one


    //! UPDATE
    // 管理员修改商品信息 : 不允许


    //! QUERY

    /**
     * 查具体商品信息 -> name + userId 确定唯一商品
     */
    @GetMapping("/one")
    @Operation(summary = "查具体商品信息")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result getByNameUser(@RequestBody ProdLocateDTO prodLocateDTO) {
        return Result.success(prodService.getByNameUser(prodLocateDTO));
    }
    //http://localhost:8085/admin/prod/one


    /**
     * 分页查询所有商品分类
     */
    @GetMapping("/cate/page")
    @Operation(summary = "分页查询所有商品分类")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageCate(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/prod/cate/page


    /**
     * 分页查询所有商品列表
     * <p>联表分页</p>
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有商品")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageProd(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.pageProd(current));
    }
    //http://localhost:8085/admin/prod/page


}
