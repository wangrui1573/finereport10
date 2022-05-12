package com.fr.function;

import com.fr.script.AbstractFunction;
import com.fr.stable.Primitive;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateDiff extends AbstractFunction
{
    private static final long serialVersionUID = -2863679010825725885L;
    public String format = null;
    private Long dif = Long.valueOf(0L);

    public Object run(Object[] paramArrayOfObject)
    {
        try
        {
            Long localLong1 = Long.valueOf(((Date)paramArrayOfObject[0]).getTime());
            Long localLong2 = Long.valueOf(((Date)paramArrayOfObject[1]).getTime());
            this.dif = Long.valueOf(localLong2.longValue() - localLong1.longValue());
            this.format = ((String)paramArrayOfObject[2]);
            replace("((?i)y)", Long.valueOf(31536000000L));
            replace("((?i)q)", Long.valueOf(7776000000L));
            replace("M", Long.valueOf(2592000000L));
            replace("((?i)w)", Long.valueOf(604800000L));
            replace("((?i)d)", Long.valueOf(86400000L));
            replace("((?i)h)", Long.valueOf(3600000L));
            replace("m", Long.valueOf(60000L));
            replace("((?i)s)", Long.valueOf(1000L));
            try
            {
                return Long.valueOf(Long.parseLong(this.format));
            }
            catch (Exception localException2)
            {
                return this.format;
            }
        }
        catch (Exception localException1)
        {
        }
        return Primitive.ERROR_VALUE;
    }

    public void replace(String paramString, Long paramLong)
    {
        Pattern localPattern = Pattern.compile(paramString + "\\{\\d*?\\}");
        Matcher localMatcher = localPattern.matcher(this.format);
        int i = (int)(this.dif.longValue() / paramLong.longValue());
        int j = 1;
        while (localMatcher.find())
        {
            String str1 = localMatcher.group();
            str1 = str1.replaceAll(paramString + "\\{", "");
            str1 = str1.replaceAll("\\}", "");
            int k = Integer.parseInt(str1);
            String str2 = String.format("%0" + k + "d", new Object[] { Integer.valueOf(i) });
            if (j != 0)
            {
                j = 0;
                this.dif = Long.valueOf(this.dif.longValue() - i * paramLong.longValue());
            }
            this.format = this.format.replaceFirst(paramString + "\\{\\d*?\\}", str2);
        }
    }
}