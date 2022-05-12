package com.fr.output;

import com.fr.schedule.base.bean.output.OutputClass;
import com.fr.schedule.base.bean.output.OutputFtp;
import com.fr.schedule.base.constant.ScheduleConstants;
import com.fr.schedule.feature.output.FTPHandler;
import com.fr.schedule.feature.output.OutputActionHandler;
import com.fr.stable.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FTPUpload extends OutputActionHandler<OutputClass> {

    private FTPHandler handler = new FTPHandler();

    @Override
    public void doAction(OutputClass action, Map<String, Object> map) throws Exception {
        OutputFtp ftp = new OutputFtp();
        ftp.setServerAddress("192.168.1.45");
        ftp.setPort(String.valueOf(21));
        ftp.setSavePath("test");
        ftp.setUsername("admin");
        ftp.setPassword("123456");

        action(ftp, map, new Filter<String>() {
            @Override
            public boolean accept(String s) {
                // TODO: 2018/8/23  过滤
                return true;
            }
        });
    }


    private void action(OutputFtp ftp, Map<String, Object> map, Filter<String> filter) throws Exception {

        String[] files = (String[]) map.get(ScheduleConstants.OUTPUT_FILES);
        List<String> fileList = new ArrayList<String>();
        for (String file : files) {
            if (filter.accept(file)) {
                fileList.add(file);
            }
        }
        map.put(ScheduleConstants.OUTPUT_FILES, fileList.toArray(new String[0]));

        handler.doAction(ftp, map);
    }

}