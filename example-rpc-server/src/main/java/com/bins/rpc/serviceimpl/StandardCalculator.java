package com.bins.rpc.serviceimpl;


import com.bins.rpc.Calculator;

/**
 * @author leo-bin
 * @date 2020/7/23 22:38
 * @apiNote 标准计数器
 */
public class StandardCalculator implements Calculator {

    @Override
    public Integer add(int a, int b) {
        return a + b;
    }
}
