package algorithm;

import document.SimilarityMatrix;
import document.TextDataset;

/**Algo: improve(改进SimilarityMatrix), getAlgorithmName*/
public interface ALGO {

    public SimilarityMatrix improve(SimilarityMatrix matrix, TextDataset textDataset);

    public String getAlgorithmName();

}
