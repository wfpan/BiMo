package ir;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import algorithm.ALGO;
import document.SimilarityMatrix;
import document.SingleLink;
import document.TextDataset;
import experiment.Result;
import experiment.project.Project;
import ir.model.IRModel;
import util.FileIOUtil;

public class YuJianLing {
	/**
	 * @param textDataset 各种数据集
	 * @param irModel     使用的模型 VSM LSI JD etc.
	 * @param algorithm   使用的算法
	 */
	public static Result compute(TextDataset textDataset, IRModel yjlModel, Project project) {
		Result result = null;
		try {
			// 注意：通过一个算法 VSM LSI JD 计算一个初始的相似度值 建凌算的是类跟类之间的，所以 这里用了 target vs target
//			SimilarityMatrix similarityMatrix = yjlModel.Compute(textDataset.getTargetCollection(),
//					textDataset.getTargetCollection());
			
			SimilarityMatrix similarityMatrix = yjlModel.Compute(textDataset.getSourceCollection(),
					textDataset.getTargetCollection());
//			SimilarityMatrix similarityMatrix = irModel.Compute(textDataset.getSourceCollection(),
//                    textDataset.getTargetCollection());

			Map<String, Map<String, Double>> sm = similarityMatrix.getMatrix();
			Set<String> ks = sm.keySet();
			Iterator<String> ksIter = ks.iterator();
			System.out.println("ksIter: "+ ks.size()+" "+ks);
			while (ksIter.hasNext()) {
				String k = ksIter.next();
				
				if (!k.equals("req1")) {
					continue;
				}

				Set<String> ks2 = sm.get(k).keySet();
				System.out.println("ks2: " + ks2);
				Iterator<String> ks2Iter = ks2.iterator();
				while (ks2Iter.hasNext()) {
					String k2 = ks2Iter.next();
					double sim = sm.get(k).get(k2);
					if (!k.equals(k2)) {
						System.out.println(k2 + " " + sim + "\n" + project.getResultDirPath() + "/"
								+ project.getProjectName() + "_" + yjlModel.getModelName() + "_rcsim.txt");
						FileIOUtil.continueWriteFile(k2 + " " + sim + "\n", project.getResultDirPath() + "/"
								+ project.getProjectName() + "_" + yjlModel.getModelName() + "_rcsim.txt");

//						FileIOUtil.continueWriteFile(k + " " + k2 + " " + sim + "\n", project.getResultDirPath() + "/"
//								+ project.getProjectName() + "_" + yjlModel.getModelName() + "_simResult.txt");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("sim计算完毕");
		return result;
	}
}
