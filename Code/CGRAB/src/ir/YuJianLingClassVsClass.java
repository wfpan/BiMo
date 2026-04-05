package ir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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

public class YuJianLingClassVsClass {
	/**
	 * @param textDataset 各种数据集
	 * @param irModel     使用的模型 VSM LSI JD etc.
	 * @param algorithm   使用的算法
	 */
	public static Result compute(TextDataset textDataset, IRModel yjlModel, Project project) {
		Result result = null;
		System.out.println("compute...");
		try {
			// 注意：通过一个算法 VSM LSI JD 计算一个初始的相似度值 建凌算的是类跟类之间的，所以 这里用了 target vs target
//			SimilarityMatrix similarityMatrix = yjlModel.Compute(textDataset.getTargetCollection(),
//					textDataset.getTargetCollection());
			
			//注意：这里求需求之间的相似性 晓龙 mashup 描述相似性
			SimilarityMatrix similarityMatrix = yjlModel.Compute(textDataset.getSourceCollection(),
					textDataset.getSourceCollection());
			
			//这个是求 req 和 class之间的
//			SimilarityMatrix similarityMatrix = yjlModel.Compute(textDataset.getSourceCollection(),
//					textDataset.getTargetCollection());
//			SimilarityMatrix similarityMatrix = irModel.Compute(textDataset.getSourceCollection(),
//                    textDataset.getTargetCollection());

			Map<String, Map<String, Double>> sm = similarityMatrix.getMatrix();
			Set<String> ks = sm.keySet();
			
			System.out.println("storing results...");
			
			List<String> kys = new ArrayList<>(ks);
			Collections.sort(kys);
			
			String path = project.getResultDirPath() + "/" + project.getProjectName() + "_" + yjlModel.getModelName() + "_desVSdes_sim.txt";
			Path outPath = Paths.get(path);
	        Charset charset = Charset.forName("UTF-8");
	        File file = new File(path);

	        FileWriter writer = null;
	        writer = new FileWriter(file, true);       
			
			for(int kk=0; kk<kys.size(); kk++) {
				System.out.println("the " + kk +"-th doc...");
				String n1 = kys.get(kk);
				for(int jj=kk+1; jj<kys.size(); jj++) {
					String n2 = kys.get(jj);
					double sim = sm.get(n1).get(n2);
					
					writer.write(n1 + " " + n2 + " " + sim + "\n");
					
					//直接调用速度太慢，输入一行就关闭了，影响效率
//					FileIOUtil.continueWriteFile(n1 + " " + n2 + " " + sim + "\n", project.getResultDirPath() + "/"
//							+ project.getProjectName() + "_" + yjlModel.getModelName() + "_desVSdes_sim.txt");
				}
			}
			
			writer.flush();
            writer.close();
			
		/*	
			Iterator<String> ksIter = ks.iterator();
//			System.out.println("ksIter: "+ ks.size()+" "+ks);
			System.out.println("storing results...");
			int ri = 0;
			while (ksIter.hasNext()) {
				System.out.println("the " + ri +"-th doc...");
				String k = ksIter.next();
				
				//原来我们的例子中 需求只有一份，所以这里这么处理了一下，现在相当于 需求有很多
//				if (!k.equals("req1")) {
//					continue;
//				}

				Set<String> ks2 = sm.get(k).keySet();
//				System.out.println("ks2: " + ks2);
				Iterator<String> ks2Iter = ks2.iterator();
				while (ks2Iter.hasNext()) {
					String k2 = ks2Iter.next();
					double sim = sm.get(k).get(k2);
					if (!k.equals(k2)) {
//						System.out.println(k + " " +k2 + " " + sim + "\n" + project.getResultDirPath() + "/"
//								+ project.getProjectName() + "_" + yjlModel.getModelName() + "_reqVSreq_rcsim.txt");
						FileIOUtil.continueWriteFile(k + " " + k2 + " " + sim + "\n", project.getResultDirPath() + "/"
								+ project.getProjectName() + "_" + yjlModel.getModelName() + "_desVSdes_sim.txt");

//						FileIOUtil.continueWriteFile(k + " " + k2 + " " + sim + "\n", project.getResultDirPath() + "/"
//								+ project.getProjectName() + "_" + yjlModel.getModelName() + "_simResult.txt");
					}
				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("sim计算完毕");
		return result;
	}
}
