package com.fr.demo;

import com.fr.base.FRContext;
import com.fr.base.Utils;
import com.fr.cache.Attachment;
import com.fr.data.DefinedVerifyJob;
import com.fr.data.JobValue;
import com.fr.data.Verifier;
import com.fr.general.FArray;
import com.fr.script.Calculator;
import com.fr.log.FineLoggerFactory;

public class VerifyJobDemo2 extends DefinedVerifyJob{
    /*
     * 必须要定义此私有变量,变量名可改，表示校验状态
     * 0  表示校验成功，默认校验状态位为0
     * 1  表示校验失败
     */
    private int type = 0;

    /**
     * 当模板自定义事件增加的属性 名称与下面变量有对应时，则会自动赋值于此对应变量
     */
    private JobValue file;   // JobValue对应单元格
    private JobValue yesOrno;
    //  private int minnum;       // 如果是非单元格，则对应具体类型值

    public void doJob(Calculator calculator) throws Exception {
        /*
         * 如这边提供一个简单的判断来模拟执行过程
         * 校验规则为yesOrno等于1 并且 file 上传了附件：yesOrno == 1 && file 上传附件
         * 校验不通过,提示“请上传附件”
         */
        //这个是fr打印日志的接口，如果是设计器下的话 会在日志里看到对应的日志信息打印出来
        FRContext.getLogger().error("##### start verigy####");
        int yn = 0;
        if(yesOrno.getValue() instanceof Integer){ //将单元格值转为整型以便用于比较
            yn = Integer.parseInt(yesOrno.getValue().toString());
        }else {
            yn = Integer.parseInt(Utils.objectToString(yesOrno.getValue()));
        }
        FineLoggerFactory.getLogger().error("##### yn = "+yn +"####");
        if (yn == 1) {
            //判断file是否有上传文件
            if (file.getValue() instanceof FArray && ((FArray) file.getValue()).length() > 0
                    && ((FArray) file.getValue()).elementAt(0) instanceof Attachment) {
                type = 0;
            } else {
                type = 1;
            }
        } else {
            type = 1;
        }
    }

    public String getMessage() {
        // 根据校验状态是成功还是失败，设置校验失败的返回信息
        if(type == 1){
            return "请上传附件";
        }
        return "";
    }
    public Verifier.Status getType() {
        // 返回校验状态
        return Verifier.Status.parse(type);
    }

    public void doFinish(Calculator arg0) throws Exception {
        // TODO Auto-generated method stub

    }

}