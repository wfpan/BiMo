package experiment.preprocess.biterm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BitermUtil {
    public static String getNewTerm(List<String> pair) {
        StringBuilder sb = new StringBuilder();
        sb.append(pair.get(0));
        for (int i = 1; i < pair.size(); i++) {
            char[] chs = pair.get(i).toCharArray();
            chs[0] = (char) (chs[0] - 32); //小写-32，转为字母大写了
            sb.append(new String(chs));
        }
        return sb.toString();
    }

    /**互相组合[a,b,c,d] -> [a,b | a,c|a,d|b,c|b,d|c,d]<br/>
     * 如：getGoodName -> get good, get name, good name<br/>
     * 返回一个个pair的list: &lt;get good&gt;*/
    public static List<List<String>> combineTwoTerm(List<String> termList) {
        List<List<String>> result = new ArrayList<>();
        if (termList.size() > 1) {
            for (int i = 0; i < termList.size(); i++) {
                for (int j = i + 1; j < termList.size(); j++) {
                    List<String> list = new LinkedList<String>();
                    if (termList.get(i).equals(termList.get(j)))
                        continue;
                    list.add(termList.get(i));
                    list.add(termList.get(j));
                    result.add(list);
                }
            }
        }
        return result;
    }
}
