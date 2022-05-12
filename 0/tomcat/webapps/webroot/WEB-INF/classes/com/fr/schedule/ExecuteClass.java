package com.fr.schedule;

import java.util.Calendar;
import com.fr.schedule.base.provider.ExecuteCondition;

public class ExecuteClass implements ExecuteCondition {
    public boolean execute() {
        Calendar cal = Calendar.getInstance();
        int dow = cal.get(Calendar.DAY_OF_WEEK);//星期二的dow等于3
        return (dow) == 3;
    }
}