/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util;

import lombok.Data;

/**
 * @author zhenmin
 * @version $Id: Pair.java, v 0.1 2024-04-08 17:16 xuxu Exp $$
 */
@Data
public class Peak<N, B, E, P> {

    private N number;

    private B begin;

    private E end;

    private P price;

    public Peak<N, B, E, P> put(N n, B b, E e, P p) {

        this.number = n;
        this.begin = b;
        this.end = e;
        this.price = p;
        return this;
    }
}