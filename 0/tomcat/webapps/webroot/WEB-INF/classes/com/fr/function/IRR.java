package com.fr.function;

import java.math.BigDecimal;

import com.fr.general.FArray;
import com.fr.general.GeneralUtils;
import com.fr.script.AbstractFunction;

public class IRR extends AbstractFunction {

    private static final long serialVersionUID = 7634415917398642321L;
    private static final String ERROR_VALUE = "#NUM!";


    @Override
    public Object run(Object[] args) {
        try{
            if(1 == args.length){
                return run( transArr( (FArray) args[0] ) );
            }else if(2 == args.length){
                return run( transArr( (FArray) args[0] ), trans( args[1] ) );
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return ERROR_VALUE;
    }

    /**
     * 将其他类型的数字转换为大数（保证精度）
     * @param ele
     * @return
     */
    private static BigDecimal trans(Object ele){
        try{
            String val = GeneralUtils.objectToString(ele);
            return new BigDecimal(val);
        }catch(Exception e){

        }
        return (BigDecimal) ele;
    }

    /**
     * 将数组转换为大数数组
     * @param in
     * @return
     */
    private static FArray<BigDecimal> transArr(FArray in){
        FArray<BigDecimal> rt = new FArray<BigDecimal>();
        for(int i=0;i<in.length();i++){
            Object ele = in.elementAt(i);
            rt.add(trans(ele));
        }
        return rt;
    }


    private static BigDecimal run(FArray<BigDecimal> cashflow){
        return run( cashflow, new BigDecimal(0.1d) );
    }

    private static BigDecimal run(FArray<BigDecimal> cashflow,BigDecimal guess){
        BigDecimal maxrate = initRateMax(cashflow,guess);
        BigDecimal minrate = initRateMin(cashflow,guess);
        for( int i=0; i<Init_Max_Loop; i++ ){
            BigDecimal testrate = minrate.add(maxrate).divide( new BigDecimal(2d) );
            BigDecimal npv = NPV( cashflow, testrate );
            if( npv.abs().compareTo(Accuracy) == LESS ){
                guess = testrate;
                break;
            }else if( npv.compareTo(ZERO) == LESS ){
                minrate = testrate;
            }else{
                maxrate = testrate;
            }
        }
        //保留16位小数（足够精度）
        return guess.setScale(16,BigDecimal.ROUND_HALF_UP);
    }

    //最小精度
    private static final BigDecimal Accuracy = new BigDecimal(0.00000001d);
    //最大循环次数，excel用的是20次，不过精度只到小数点后两位，而且不一定一定能算出值，为了尽可能保证算出结果，我增加到100次，
    private static final int Init_Max_Loop = 100;

    private static final BigDecimal ZERO = new BigDecimal(0);
    private static final BigDecimal ONE = new BigDecimal(1);
    private static final BigDecimal Z005 = new BigDecimal(0.005d);
    private static final BigDecimal Z2 = new BigDecimal(0.2d);

    private static final int GREATER = 1;
    private static final int LESS = -1;

    /**
     * 生成一个使NPV为负数的R作为内部收益率下限值
     * @param cashflow
     * @param guess
     * @return
     */
    private static BigDecimal initRateMin(FArray<BigDecimal> cashflow,BigDecimal guess){
        for( int i=0; i<Init_Max_Loop; i++ ){
            BigDecimal npv = NPV( cashflow, guess );

            if( npv.compareTo(ZERO) == LESS ){
                return guess;
            }
            BigDecimal step = guess.abs().multiply( Z2 );
            guess = guess.add( step.compareTo( Z005 ) == LESS ? Z005 : step );
        }
        return guess;
    }

    /**
     * 生成一个使NPV为正数的R作为内部收益率的上限制
     * @param cashflow
     * @param guess
     * @return
     */
    private static BigDecimal initRateMax(FArray<BigDecimal> cashflow,BigDecimal guess){
        for( int i=0; i<Init_Max_Loop; i++ ){
            BigDecimal npv = NPV( cashflow, guess );

            if( npv.compareTo(ZERO) == GREATER ){
                return guess;
            }
            BigDecimal step = guess.abs().multiply( Z2 );
            guess = guess.subtract( step.compareTo( Z005 ) == LESS ? Z005 : step );
        }
        return guess;
    }

    /**
     * 算NPV
     * @param cashflow
     * @param rate
     * @return
     */
    private static BigDecimal NPV(FArray<BigDecimal> cashflow,BigDecimal rate){
        BigDecimal npv = ZERO;
        BigDecimal rpowj = ONE;//(1+r)^0
        BigDecimal radd1 = rate.add(ONE);//1+r
        for( int j=0; j<cashflow.length(); j++ ){
            BigDecimal valuej = cashflow.elementAt(j);
            npv = npv.add( valuej.divide( rpowj, 10, BigDecimal.ROUND_HALF_DOWN ) );// vj / (1+r)^j
            rpowj = rpowj.multiply(radd1); // (1+r)^j
            //npv += cashflow.elementAt(j) / Math.pow( 1+rate, j );
        }
        return npv;
    }

}