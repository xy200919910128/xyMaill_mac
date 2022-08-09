package com.xy.auth.schedleTest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadImage {

    public void exportImageTest(HttpServletResponse response){
        InputStream inputStream = null;
        HttpURLConnection url = null;
        try {
            url = (HttpURLConnection) new URL("https://img2.baidu.com/it/u=1482175266,3706609728&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=750").openConnection();
            url.connect();
            inputStream = url.getInputStream();
            response.setContentType("application/force-download");
            response.addHeader("Content-disposition","attachment;filename=11.jpg");
            int len;
            byte[] buf = new byte[1024];
            while ((len=inputStream.read(buf))!=-1){
                response.getOutputStream().write(buf,0,len);
                response.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                response.getOutputStream().close();
                url.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
