package experiment.preprocess;

public class TextPreprocess {
    private String str;
//    private String stopwordsPath = "src/stopwords.txt";
    private String stopwordsPath = "src/stopWordList.txt";

    public TextPreprocess(String str) {
        this.str = str;
    }

    /**1.非字母删除 <br/>
     * 2、骆峰原则处理 <br/>
     * 3.单词长度过滤 <br/>
     * 4.大小写转化<br/> 
     * 5.词干提取<br/>
     * 6.停用词处理*/
    public String doReqTextProcess() {
        str = CleanUp.chararctorClean(str);
        str = CamelCase.split(str);
        str = CleanUp.lengthFilter(str, 3);
        str = CleanUp.tolowerCase(str);
        str = Stopwords.remover(str, stopwordsPath);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, stopwordsPath);
        return str;
    }

    /**1.非字母删除 <br/>
     * 2、骆峰原则处理 <br/>
     * 3.单词长度过滤 <br/>
     * 4.大小写转化<br/> 
     * 5.词干提取<br/>
     * 6.停用词处理*/
    public String doCodeTextProcess() {
        str = CleanUp.chararctorClean(str);
        str = CamelCase.split(str);
        str = CleanUp.lengthFilter(str, 3);
        str = CleanUp.tolowerCase(str);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, stopwordsPath);
        return str;
    }

}
