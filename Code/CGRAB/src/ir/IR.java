package ir;

import algorithm.ALGO;
import document.SimilarityMatrix;
import document.SingleLink;
import document.TextDataset;
import experiment.Result;
import ir.model.IRModel;

public class IR {

	/**@param textDataset 各种数据集
	 * @param irModel 使用的模型 VSM LSI JD etc.
	 * @param algorithm 使用的算法*/
    public static Result compute(TextDataset textDataset, IRModel irModel, ALGO algorithm) {
        Result result = null;
        SimilarityMatrix matrix_improve = new SimilarityMatrix();

        try {
        	//通过一个算法 VSM LSI JD 计算一个初始的相似度值
            SimilarityMatrix similarityMatrix = irModel.Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            
            //对相似度结果进行改进
            SimilarityMatrix matrix = algorithm.improve(similarityMatrix, textDataset);
            
            //大于阈值保留，小于阈值为 0
            for (SingleLink link : matrix.allLinks()) {
                if (similarityMatrix.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
                    matrix_improve.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
                } else {
                    matrix_improve.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), 0.0);
                }
            }
            
            result = new Result(matrix_improve, textDataset.getRtm());
            result.setAlgorithmName(algorithm.getAlgorithmName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.setModel(irModel.toString());
        return result;
    }
}
