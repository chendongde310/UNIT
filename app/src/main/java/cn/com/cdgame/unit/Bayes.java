package cn.com.cdgame.unit;

import android.content.Context;

import com.hankcs.hanlp.HanLP;
import com.orhanobut.logger.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 贝叶斯定理
 * P(A|B) = P(B|A) P(A) / P(B)
 * <p>
 * A = 分类      B = （T1*T2*T3*...Tn ）   T = Term 分词
 * <p>
 * P(A|T1*T2*T3*...Tn )
 * = P(T1|A)*P(T2|A)*P(T3|A)*...P(Tn|A)*P(A)
 * /(P(T1)*P(T2)*P(T3)*...P(Tn))
 */

public class Bayes {
    Context context;
    List<Classification> CFList = new ArrayList<>();
    Map<String, Integer> wordAllFrequency = new HashMap<>();
    double wordAllNum = 0d;

    public Bayes(Context context) {
        this.context = context;

        try {
            loadSample();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    private void loadSample() throws IOException, DocumentException {


        SAXReader reader = new SAXReader();
        Document document = reader.read(context.getApplicationContext().getAssets().open("sample.xml"));
        List<Element> samples = document.getRootElement().elements("sample");
        for (Element s : samples) {
            Classification c = new Classification();
            c.name = s.attributeValue("class");
            List<Element> as = s.elements("a");
            Map<String, Integer> wordFrequency = new HashMap<>();
            for (Element a : as) {
                for (String key : HanLP.extractKeyword(a.getTextTrim(), 6)) {
                    if (wordFrequency.containsKey(key)) {
                        wordFrequency.put(key, wordFrequency.get(key) + 1);
                    } else {
                        wordFrequency.put(key, 1);
                    }
                    c.wordNum++;

                    if (wordAllFrequency.containsKey(key)) {
                        wordAllFrequency.put(key, wordAllFrequency.get(key) + 1);
                    } else {
                        wordAllFrequency.put(key, 1);
                    }
                    wordAllNum++;

                }

            }
            c.wordFrequency = wordFrequency;
            CFList.add(c);
        }

    }

    /**
     * P(A|T1*T2*T3*...Tn )
     * = P(T1|A)*P(T2|A)*P(T3|A)*...P(Tn|A)*P(A)
     * /(P(T1)*P(T2)*P(T3)*...P(Tn))
     */
    public Classification operation(String text) {
        double PFlag = 100;
        Classification FlagC = new Classification();
        Classification MaxC = new Classification();
        for (Classification c : CFList) {
            List<String> keys = HanLP.extractKeyword(text, 6);
            double DA = 1.00;
            double DB = 1.00;
            for (String s : keys) {
                //判断有没有这个词
                if (c.wordFrequency.containsKey(s)) {
//                    double d1 = c.wordFrequency.get(s);
//                    double d2 = c.wordFrequency.get(s);
                    DA = DA * (c.wordFrequency.get(s) / c.wordNum);
                }

                if (wordAllFrequency.containsKey(s)) {
                    DB = DB * (wordAllFrequency.get(s) / wordAllNum);
                }


            }
            double d1 = CFList.size();
            DA = DA * (1 / d1);
            if (PFlag > DA / DB) {
                PFlag = DA / DB;
                MaxC = c;
            }
            Logger.d("Flag:" + PFlag);
        }
        return MaxC;
    }


    public static class Classification {
        public String name;
        public Map<String, Integer> wordFrequency;
        public double wordNum = 0d;

        public Classification() {
            name = "无意图";
        }

        @Override
        public String toString() {
            Logger.e(name);
            return name;
        }
    }


}
