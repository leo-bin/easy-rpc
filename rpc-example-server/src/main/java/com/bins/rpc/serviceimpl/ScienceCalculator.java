package com.bins.rpc.serviceimpl;

import com.bins.rpc.Calculator;

/**
 * @author leo-bin
 * @date 2020/7/25 17:53
 * @apiNote 科学计数器
 */
public class ScienceCalculator implements Calculator {

    @Override
    public Integer add(int a, int b) {
        return a + b;
    }
}
