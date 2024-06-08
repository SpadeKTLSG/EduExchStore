package com.shop.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.context.UserHolder;
import com.shop.common.exception.BadArgsException;
import com.shop.common.exception.SthNotFoundException;
import com.shop.common.exception.TrashException;
import com.shop.pojo.dto.VoucherAllDTO;
import com.shop.pojo.dto.VoucherLocateDTO;
import com.shop.pojo.entity.Order;
import com.shop.pojo.entity.UserFunc;
import com.shop.pojo.entity.Voucher;
import com.shop.serve.mapper.VoucherMapper;
import com.shop.serve.service.OrderService;
import com.shop.serve.service.UserFuncService;
import com.shop.serve.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.shop.common.constant.MessageConstants.*;
import static com.shop.common.constant.TestsConstants.BUYER_USERID;
import static com.shop.common.constant.TestsConstants.STORE_USERID;

@Slf4j
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    @Autowired
    private UserFuncService userFuncService;
    @Autowired
    private OrderService orderService;

    @Override
    public void addSeckillVoucher(VoucherAllDTO voucherAllDTO) {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherAllDTO, voucher);
        voucher.setUserId(1L);
        voucher.setType(1);
        this.save(voucher);
    }


    @Override
    @Transactional
    public void addVoucher(VoucherAllDTO voucherAllDTO) {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherAllDTO, voucher);
        voucher.setUserId(STORE_USERID); //存到默认仓库用户
        this.save(voucher);
    }


    @Override
    @Transactional
    public void claimVoucher(VoucherLocateDTO voucherLocateDTO) {

        Voucher voucher = this.getOne(new LambdaQueryWrapper<Voucher>()
                .eq(Voucher::getName, voucherLocateDTO.getName())
                .eq(Voucher::getUserId, STORE_USERID));

        if (voucher == null) throw new SthNotFoundException(OBJECT_NOT_ALIVE);

        if (voucher.getStock() <= 0) throw new SthNotFoundException(OBJECT_NOT_ALIVE);


        voucher.setStock(voucher.getStock() - 1);

        //创建一个新的卷对象, 用于用户使用
        Voucher newVoucher = new Voucher();
        BeanUtils.copyProperties(voucher, newVoucher);
        newVoucher.setId(null); //清空ID, 重新插入
        newVoucher.setName(voucher.getName() + UserHolder.getUser().getId() + "-已领取");
        newVoucher.setUserId(UserHolder.getUser().getId());
        newVoucher.setStock(1);


        this.updateById(voucher);
        this.save(newVoucher);
    }

    @Override
    @Transactional
    public Integer useVoucher4Seller(VoucherLocateDTO voucherLocateDTO) {

        Voucher voucher = this.getOne(new LambdaQueryWrapper<Voucher>()
                .eq(Voucher::getName, voucherLocateDTO.getName())
                .eq(Voucher::getUserId, UserHolder.getUser().getId()));

        if (voucher == null) throw new SthNotFoundException(OBJECT_NOT_ALIVE);

        if (Objects.equals(voucher.getStatus(), Voucher.USED) || Objects.equals(voucher.getStatus(), Voucher.OUTDATE) || voucher.getStock() == 0)
            throw new TrashException(TRASH_ERROR);

        if (Objects.equals(voucher.getUser(), Voucher.BUYER)) throw new BadArgsException(BAD_ARGS);


        //执行功能: 修改当前Voucher对象
        voucher.setStatus(Voucher.USED);
        voucher.setBeginTime(LocalDateTime.now());

        if (voucher.getFunc() == 0) { //基础功能类型
            voucher.setEndTime(LocalDateTime.now().plusDays(1)); // 1天
        } else if (voucher.getFunc() == 1) { //高级功能类型
            voucher.setEndTime(LocalDateTime.now().plusDays(3)); // 3天
        } else if (voucher.getFunc() == 2) { //超级功能类型
            voucher.setEndTime(LocalDateTime.now().plusDays(7)); // 7天
        }

        this.updateById(voucher);


        //用户增加嘉奖值
        UserFunc userFunc = userFuncService.getById(UserHolder.getUser().getId());

        int value2Add = voucher.getType() == 0 ? voucher.getValue() : voucher.getValue() * 2;

        userFunc.setCredit(userFunc.getCredit() + value2Add);
        userFuncService.updateById(userFunc);

        //回传给前端效果字段 : voucher.getFunc(), 0 - 1 - 2 指明其功能类型, 用于后续前端打开窗口,
        // 让用户指定商品对象进行Update操作-> ProdFunc字段修改, 请求见ProdController
        return voucher.getFunc();
    }


    @Override
    @Transactional
    public boolean useVoucher4Buyer(VoucherLocateDTO voucherLocateDTO) {

        Voucher voucher = this.getOne(new LambdaQueryWrapper<Voucher>()
                .eq(Voucher::getName, voucherLocateDTO.getName())
                .eq(Voucher::getUserId, UserHolder.getUser().getId()));

        if (voucher == null) throw new SthNotFoundException(OBJECT_NOT_ALIVE);

        if (Objects.equals(voucher.getStatus(), Voucher.USED) || Objects.equals(voucher.getStatus(), Voucher.OUTDATE) || voucher.getStock() == 0)
            throw new TrashException(TRASH_ERROR);

        if (Objects.equals(voucher.getUser(), Voucher.SELLER)) throw new BadArgsException(BAD_ARGS);


        //执行功能: 修改当前Voucher对象
        voucher.setStatus(Voucher.USED);
        voucher.setBeginTime(LocalDateTime.now());

        if (voucher.getFunc() == 0) { //基础功能类型
            voucher.setEndTime(LocalDateTime.now().plusDays(1)); // 1天
        } else if (voucher.getFunc() == 1) { //高级功能类型
            voucher.setEndTime(LocalDateTime.now().plusDays(3)); // 3天
        } else if (voucher.getFunc() == 2) { //超级功能类型
            voucher.setEndTime(LocalDateTime.now().plusDays(7)); // 7天
        }

        this.updateById(voucher);

        //用户增加嘉奖值
        UserFunc userFunc = userFuncService.getById(UserHolder.getUser().getId());

        int value2Add = voucher.getType() == 0 ? voucher.getValue() : voucher.getValue() * 2;

        userFunc.setCredit(userFunc.getCredit() + value2Add);
        userFuncService.updateById(userFunc);

        //对目前开启的交易判定是否存在, 存在则视为一次准入成功, 对用户进行增加嘉奖值操作(否则没有奖励)

        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerId, BUYER_USERID));

        if (order == null) return false;

        UserFunc userFunc2 = userFuncService.getById(BUYER_USERID);
        userFunc2.setGodhit(userFunc2.getGodhit() + 1);
        userFuncService.updateById(userFunc2);

        return true;
    }


    @Override
    public List<Voucher> getOutdateOnes(Integer status, LocalDateTime time) {
        List<Voucher> voucherList2Check = this.query()
                .eq("status", status)
                .list();

        //需要手动取出来判断是否过期
        voucherList2Check.removeIf(voucher -> voucher.getEndTime().isAfter(time));

        return voucherList2Check;
    }

    @Override
    public void ruinVoucher(Voucher voucher) {
        voucher.setStatus(2);
        this.updateById(voucher);
    }


}
