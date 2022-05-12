package com.fr.function;
import com.fr.script.AbstractFunction;
import com.fr.stable.Primitive;
public class FlagHtmlColor extends AbstractFunction {
    public Object run(Object[] args) {
        String  txt="";
        String color="";
        String flag="";
        if(null==args||args.length==0){
            return Primitive.ERROR_NAME;
        }else if(args.length==1){
            txt=args[0].toString();
            color="red";
            flag="N";
        }else if(args.length==2){
            txt=args[0].toString();
            color=args[1].toString();
            flag="N";
        }else{
            txt=args[0].toString();
            color=args[1].toString();
            flag=args[2].toString();
        }
        String result=getHtmlStr(txt, color, flag);
        return result;
    }
    public String getHtmlStr(String txt,String  color,String flag){
        String starthtml="<font color='"+color+"'>";
        String endhtml="</font>";
        StringBuffer sb=new StringBuffer();
        int len=txt.length();
        if("N".equalsIgnoreCase(flag)){//数字
            for(int i=0;i<len;i++){
                char c=txt.charAt(i);
                if(c>='0'&&c<='9'){
                    String str=starthtml+c+endhtml;
                    sb.append(str);
                }else{
                    sb.append(c);
                }
            }
        }else if("C".equalsIgnoreCase(flag)){//字母
            for(int i=0;i<len;i++){
                char c=txt.charAt(i);
                if((c>='a'&&c<='z')||(c>='A'&&c<='Z')){
                    String str=starthtml+c+endhtml;
                    sb.append(str);
                }else{
                    sb.append(c);
                }
            }
        } else if("Z".equalsIgnoreCase(flag)){//中文
            for(int i=0;i<len;i++){
                char c=txt.charAt(i);
                if(c >= 0x4E00 &&  c <= 0x9FA5){
                    String str=starthtml+c+endhtml;
                    sb.append(str);
                }else{
                    sb.append(c);
                }
            }
        }else{
            return txt;
        }
        return sb.toString();
    }
}