package com.shop.guest.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.context.UserHolder;
import com.shop.pojo.Result;
import com.shop.pojo.dto.VoucherLocateDTO;
import com.shop.pojo.entity.Voucher;
import com.shop.pojo.vo.VoucherStoreVO;
import com.shop.serve.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.shop.common.constant.SystemConstants.MAX_PAGE_SIZE;
import static com.shop.common.constant.TestsConstants.STORE_USERID;

/**
 * 优惠券控制
 *
 * @author SK
 * @date 2024/06/03
 */
@Slf4j
@Tag(name = "Voucher", description = "优惠券")
@RequestMapping("/guest/voucher")
@RestController
public class VoucherController {

    @Autowired
    private VoucherService voucherService;


    //! Func


    //! ADD
    //禁止


    //! DELETE


    /**
     * 使用自己的卖方优惠券
     * <p>进行优惠券功能时需要判断权限和对象</p>
     * <p>保留式删除</p>
     */
    @Transactional
    @DeleteMapping("/use/seller")
    @Operation(summary = "使用自己的卖方优惠券")
    @Parameters(@Parameter(name = "voucherStoreDTO", description = "优惠券存储DTO", required = true))
    public Result useVoucher4Seller(@RequestBody VoucherLocateDTO voucherLocateDTO) {
        return Result.success(voucherService.useVoucher4Seller(voucherLocateDTO));
    }
    //http://localhost:8086/guest/voucher/use/seller


    /**
     * 使用自己的买方优惠券 -> 下一步直接发起交易,
     * <p>进行优惠券功能时需要判断权限和对象</p>
     * <p>保留式删除</p>
     */
    @Transactional
    @DeleteMapping("/use/buyer")
    @Operation(summary = "使用自己的买方优惠券")
    @Parameters(@Parameter(name = "voucherStoreDTO", description = "优惠券存储DTO", required = true))
    public Result useVoucher4Buyer(@RequestBody VoucherLocateDTO voucherLocateDTO) {
        return voucherService.useVoucher4Buyer(voucherLocateDTO) ? Result.success(true) : Result.success(false);  //是否能成功获得Bonus
    }
    //http://localhost:8086/guest/voucher/use/buyer


    //! UPDATE

    //后续引入秒杀流程, 管理员发布后执行


    /**
     * 宣称(领取)优惠券
     * <p>不可重复领取</p>
     */
    @PutMapping("/claim")
    @Operation(summary = "宣称(领取)优惠券")
    @Parameters(@Parameter(name = "voucherLocateDTO", description = "优惠券定位DTO", required = true))
    public Result claimVoucher(@RequestBody VoucherLocateDTO voucherLocateDTO) {
        voucherService.claimVoucher(voucherLocateDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/voucher/claim


    //! QUERY

    /**
     * 分页查询仓库里卖方优惠券列表
     * <p>之后点击可以领取(一出就可以)</p>
     */
    @GetMapping("/page/seller")
    @Operation(summary = "分页仓库里卖方优惠券列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageVoucher4Seller(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        return Result.success(voucherService.page(new Page<>(current, MAX_PAGE_SIZE), new LambdaQueryWrapper<Voucher>()
                        .eq(Voucher::getUser, STORE_USERID))
                .convert(voucher -> {
                    VoucherStoreVO voucherStoreVO = new VoucherStoreVO();
                    BeanUtils.copyProperties(voucher, voucherStoreVO);
                    return voucherStoreVO;
                }));
    }
    //http://localhost:8086/guest/voucher/page/seller


    /**
     * 分页查询仓库里买方优惠券列表
     * <p>之后点击可以领取(一出就可以)</p>
     */
    @GetMapping("/page/buyer")
    @Operation(summary = "分页仓库里买方优惠券列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageVoucher4Buyer(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        return Result.success(voucherService.page(new Page<>(current, MAX_PAGE_SIZE), new LambdaQueryWrapper<Voucher>()
                        .eq(Voucher::getUser, STORE_USERID))
                .convert(voucher -> {
                    VoucherStoreVO voucherStoreVO = new VoucherStoreVO();
                    BeanUtils.copyProperties(voucher, voucherStoreVO);
                    return voucherStoreVO;
                }));
    }
    //http://localhost:8086/guest/voucher/page/buyer


    /**
     * 分页查询自己的卖方优惠券列表
     * <p>之后点击可以使用</p>
     */
    @GetMapping("/me/page/seller")
    @Operation(summary = "分页自己卖方优惠券列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageMyVoucher4Seller(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        return Result.success(voucherService.page(new Page<>(current, MAX_PAGE_SIZE), new LambdaQueryWrapper<Voucher>()
                        .eq(Voucher::getUser, STORE_USERID)
                        .eq(Voucher::getUserId, UserHolder.getUser().getId()))
                .convert(voucher -> {
                    VoucherStoreVO voucherStoreVO = new VoucherStoreVO();
                    BeanUtils.copyProperties(voucher, voucherStoreVO);
                    return voucherStoreVO;
                }));
    }
    //http://localhost:8086/guest/voucher/me/page/seller


    /**
     * 分页查询自己的买方优惠券列表
     * <p>之后点击可以使用</p>
     */
    @GetMapping("/me/page/buyer")
    @Operation(summary = "分页自己买方优惠券列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageMyVoucher4Buyer(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        return Result.success(voucherService.page(new Page<>(current, MAX_PAGE_SIZE), new LambdaQueryWrapper<Voucher>()
                        .eq(Voucher::getUser, STORE_USERID)
                        .eq(Voucher::getUserId, UserHolder.getUser().getId()))


                .convert(voucher -> {
                    VoucherStoreVO voucherStoreVO = new VoucherStoreVO();
                    BeanUtils.copyProperties(voucher, voucherStoreVO);
                    return voucherStoreVO;
                }));
    }
    //http://localhost:8086/guest/voucher/me/page/buyer

}
