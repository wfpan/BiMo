package document;


import util.FileIOUtil;
import util.ReadRtmUtil;

public class TextDataset {
	/**sourceDirPath:processed\req*/
    private ArtifactsCollection sourceCollection;
    /**processed\code\class*/
    private ArtifactsCollection targetCollection;

    private SimilarityMatrix rtm;

    /**
     * sourceDirPath: uc files' directory path 处理后的 req 文本（好像合并了description和summary）<br/>
     * targetDirPath: class code files' directory path 处理后的类的文本<br/>
     * rtmPath: 得到 dataset 下 某一个 project 下 rtm/RTM_CLASS.txt<br/>
     * 读取文件保存为Map、读取连接关系也保存<br/>
     * sourceDirPath:processed\req<br/>
     * targetDirPath:processed\code\class<br/>
     */
    public TextDataset(String sourceDirPath, String targetDirPath, String rtmPath) {
    	System.out.println("[INFO] Reading dir: " + targetDirPath);
        this.setSourceCollection(FileIOUtil.getCollections(sourceDirPath, ".txt"));
        this.setTargetCollection(FileIOUtil.getCollections(targetDirPath, ".txt"));
        this.setRtm(ReadRtmUtil.createSimilarityMatrix(rtmPath));
    }
    //计算req 与 req 之间的相似性
    public TextDataset(String sourceDirPath, String targetDirPath, String rtmPath, boolean req) {
    	System.out.println("[INFO] Reading dir: " + sourceDirPath);
    	ArtifactsCollection ac = FileIOUtil.getCollections(sourceDirPath, ".txt");
//        this.setSourceCollection(FileIOUtil.getCollections(sourceDirPath, ".txt"));
//        this.setTargetCollection(FileIOUtil.getCollections(sourceDirPath, ".txt"));
    	this.setSourceCollection(ac);
    	this.setTargetCollection(ac);
        this.setRtm(ReadRtmUtil.createSimilarityMatrix(rtmPath));
    }

    public TextDataset(ArtifactsCollection sourceCollection, ArtifactsCollection targetCollection, SimilarityMatrix rtm) {
        this.setSourceCollection(sourceCollection);
        this.setTargetCollection(targetCollection);
        this.setRtm(rtm);
    }

    /**构建map&lt;id, artifact&lt;id,context&gt;&gt; <br/>
     * req1.txt -> id=req1 as key, its context as value<br/>
     * sourceDirPath:processed\req*/
    public ArtifactsCollection getSourceCollection() {
        return sourceCollection;
    }

    public void setSourceCollection(ArtifactsCollection sourceCollection) {
        this.sourceCollection = sourceCollection;
    }

    /**processed\code\class*/
    public ArtifactsCollection getTargetCollection() {
        return targetCollection;
    }

    public void setTargetCollection(ArtifactsCollection targetCollection) {
        this.targetCollection = targetCollection;
    }

    public SimilarityMatrix getRtm() {
        return rtm;
    }

    public void setRtm(SimilarityMatrix rtm) {
        this.rtm = rtm;
    }
}
