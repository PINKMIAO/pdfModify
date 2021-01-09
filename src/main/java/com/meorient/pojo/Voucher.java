package com.meorient.pojo;

import lombok.AllArgsConstructor;

/**
 * 展会信息
 */
@AllArgsConstructor
public class Voucher {
    private String customer;
    private String exhibition;


    public String getCustomer() {
        return customer;
    }

    public String getExhibition() {
        return exhibition;
    }
}
