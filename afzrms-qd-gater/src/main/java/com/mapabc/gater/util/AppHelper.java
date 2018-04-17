package com.mapabc.gater.util;

import java.net.URL;

public class AppHelper {
    private AppHelper() {
    }

    /**
     * 得到web目录所在的绝对路径
     * @return String web目录所在的绝对路径
     */
    public static String getWebAppPath() {
        AppHelper util = new AppHelper();

        String clazzFilePath = util.getClassFilePath();

        String webPath = clazzFilePath.substring(1,
                                                 clazzFilePath.indexOf(
                "WEB-INF"));
        return webPath;
    }
    
    /**
     * 得到web名称
     * @return String web名称
     */
    public static String getWebAppName() {
        
        String webPath =  getWebAppPath();
        int i = webPath.lastIndexOf("/");
        webPath = webPath.substring(0, i) ;
        String appName = webPath.substring(webPath.lastIndexOf("/")+1);
        
        return appName;
    }

    /**
     * 获取classes文件的绝对路径
     * @return String
     */
    private String getClassFilePath() {
        String strClassName = getClass().getName(); //获取类文件名
        String strPackageName = "";
        if (getClass().getPackage() != null) {
            strPackageName = getClass().getPackage().getName();
        }
        String strClassFileName = "";
        if (!"".equals(strPackageName)) {
            strClassFileName = strClassName.substring(strPackageName.length() +
                    1, strClassName.length());
        } else {
            strClassFileName = strClassName;
        }
        URL url = null;
        url = getClass().getResource(strClassFileName + ".class");

        String strURL = url.getFile();
        try {
            strURL = java.net.URLDecoder.decode(strURL, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*strURL = strURL.substring(strURL.indexOf('/') + 1,
                                  strURL.lastIndexOf('/'))*/
        ;

        return strURL;
    }

    public static void main(String[] args) {
        System.out.println(AppHelper.getWebAppName());
    }

}
