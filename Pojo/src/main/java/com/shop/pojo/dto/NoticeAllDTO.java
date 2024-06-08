package com.shop.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告完全DTO
 *
 * @author SK
 * @date 2024/06/07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeAllDTO {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否置顶
     */
    private Boolean top = false;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
