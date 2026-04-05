package experiment.project;

/**define a series of methods to retrieve different paths, ex: <i>getRtmClassPath</i>*/
public interface Project {
	/**得到 dataset 下 某一个 project 下 rtm/RTM_CLASS.txt*/
    public String getRtmClassPath();

    /**得到 dataset 下 某一个 project 下 processed/req*/
    public String getReqPath();

    /**得到 dataset 下 某一个 project 下 processed/code/class*/
    public String getClassDirPath();

    public String getProjectName();

    public String getProjectPath();

    /**processedPath/code/part/class_name*/
    public String getClsNameDirPath();
    /**processedPath/code/part/method_name*/
    public String getMethodNameDirPath();

    public String getCommentDirPath();
    /**processedPath/code/part/invoke_method*/
    public String getInvokeMethodDirPath();

    public String getFieldNameDirPath();

    public String getFieldTypeDirPath();

    public String getParamNameDirPath();

    public String getParamTypeDirPath();

    public String getUnprocessedClsNameDirPath();

    public String getUnprocessedMethodNameDirPath();

    public String getUnprocessedCommentDirPath();

    public String getUnprocessedInvokeMethodDirPath();

    public String getUnprocessedFieldNameDirPath();

    public String getUnprocessedFieldTypeDirPath();

    public String getUnprocessedParamNameDirPath();

    public String getUnprocessedParamTypeDirPath();

    public String getClsNameBitermDirPath();

    public String getMethodNameBitermDirPath();

    public String getCommentBitermDirPath();

    public String getInvokeMethodBitermDirPath();

    public String getFieldNameBitermDirPath();

    public String getFieldTypeBitermDirPath();

    public String getParamNameBitermDirPath();

    public String getParamTypeBitermDirPath();


    public String getUnprocessedUcTitleDirPath();

    public String getUnprocessedUcPreconDirPath();

    public String getUnprocessedUcMainflowDirPath();

    public String getUnprocessedUcSubflowDirPath();

    public String getUnprocessedUcAlterflowDirPath();

    public String getUcTitleBitermDirPath();

    public String getUcPreconBitermDirPath();

    public String getUcMainflowBitermDirPath();

    public String getUcSubflowBitermDirPath();

    public String getUcAlterflowBitermDirPath();

    /**unprocessedPath/req/summary*/
    public String getUnprocessedIssueSummDirPath();

    /**unprocessedPath/req/description*/
    public String getUnprocessedIssueDescripDirPath();

    /**processedPath/req_biterm/summary*/
    public String getIssueSummBitermDirPath();

    /**processedPath/req_biterm/description*/
    public String getIssueDescripBitermDirPath();
    
    /**result's dir*/
    public String getResultDirPath();
}
