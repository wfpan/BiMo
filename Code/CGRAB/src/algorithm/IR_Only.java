package algorithm;

import document.SimilarityMatrix;
import document.TextDataset;

/**Algo: improve(改进SimilarityMatrix=IR_only不改进), getAlgorithmName*/
public class IR_Only implements ALGO {
    @Override
    public SimilarityMatrix improve(SimilarityMatrix matrix, TextDataset textDataset) {
        return matrix;
    }

    @Override
    public String getAlgorithmName() {
        return "IR-ONLY";
    }

}
