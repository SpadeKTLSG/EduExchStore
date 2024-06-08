package com.shop.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotsearchDTO {


    /**
     * 对应商品ID
     */
    private Long prodId;

    /**
     * 对应商品名
     */
    private String name;

    /**
     * 对应商品浏览量
     */
    private Long visit;
}
