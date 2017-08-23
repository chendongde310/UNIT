package cn.com.cdgame.units;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author：陈东
 * Time：2017/8/21 - 下午2:57
 * Notes:数据库管理
 */

public class DataBaseHandler {

    public static void updateDB(Context c, String fileName) throws IOException {
        InputStream is = c.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        int line = 1;
        String tempString = null;
        // 一次读入一行，直到读入null为文件结束
        while ((tempString = reader.readLine()) != null) {

            line++;
        }
    }

}
