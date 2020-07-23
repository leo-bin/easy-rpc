package com.bins.rpc;


/**
 * @author leo-bin
 * @date 2020/7/23 22:38
 * @apiNote 计算器服务的实现类
 */
public class CalculatorImpl implements Calculator {

    @Override
    public Integer add(int a, int b) {
        return a + b;
    }
}
