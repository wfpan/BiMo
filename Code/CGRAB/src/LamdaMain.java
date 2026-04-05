import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.eclipse.osgi.internal.container.ComputeNodeOrder;

import algorithm.ALGO;
import document.ArtifactsCollection;
import document.LinksList;
import document.SimilarityMatrix;
import document.SingleLink;
import document.TermDocumentMatrix;
import document.TextDataset;
import experiment.Result;
import experiment.enums.LayerEnum;
import experiment.enums.ProjectEnum;
import experiment.preprocess.biterm.ConsensualBiterm;
import experiment.project.Project;
import ir.model.IRModel;
import ir.util.IRUtil;
import ir.util.WeightUtil;
import util.FileIOUtil;

public class LamdaMain {
	private static ProjectEnum projectEnum;
	private static Boolean isUcType = false;

	public static void main(String[] args) throws Exception {
//		Class projectClass = Class.forName(ProjectEnum.TEST.getName()); // select a project, ex: Test
//		Class projectClass = Class.forName(ProjectEnum.ant_main.getName());
//		Class projectClass = Class.forName(ProjectEnum.argouml.getName());
//		Class projectClass = Class.forName(ProjectEnum.xuml.getName());
		Class projectClass = Class.forName(ProjectEnum.wro4j.getName());
		
		
		Project project = (Project) projectClass.newInstance();
		TextDataset textDataset = new TextDataset(project.getReqPath(), project.getClassDirPath(),
				project.getRtmClassPath());
		
		//清理存放结果的目录
		FileIOUtil.initDirectory(project.getResultDirPath());
		System.out.println("[INFO] Cleaning dir: " + project.getResultDirPath());
		
		new LamdaMain().compute(project, textDataset);
		
		System.out.println("[INFO] Finish. See the results in " + project.getResultDirPath());

	}

	public void compute(Project project, TextDataset textDataset) {
		projectEnum = ProjectEnum.getProject(project.getProjectName());

		System.out.println("[INFO] Computing consensual Biterms both in documentation and classes...");
		// get consensual biterms
		ConsensualBiterm consensualBiterm = new ConsensualBiterm(project);
		SimilarityMatrix similarityMatrix;

		// <reg,<biterm,count>>
		Map<String, Map<String, Integer>> reqBitermNumMap = consensualBiterm.getReqBitermsMap();
		// <code,<biterm,count>>
		Map<String, Map<String, Integer>> codeBitermNumMap = consensualBiterm.getCodeBitermsMap();
		// req
		Map<String, Map<LayerEnum, Map<String, Integer>>> reqLayerBitermNumMap = consensualBiterm
				.getReqLayerBitermNumMap();

		System.out.println("[INFO] Updating dataset with the consensual biterms...");
		textDataset = consensualBiterm.updateTextDataSet(textDataset, reqBitermNumMap, codeBitermNumMap);

		ArtifactsCollection reqCollection = textDataset.getSourceCollection();
		ArtifactsCollection clsCollection = textDataset.getTargetCollection();

		ArtifactsCollection reqCodeArtifacts = new ArtifactsCollection();
		reqCodeArtifacts.putAll(reqCollection);
		reqCodeArtifacts.putAll(clsCollection);

		System.out.println("[INFO] Building term document matrix...");
		TermDocumentMatrix reqTermMarix = new TermDocumentMatrix(reqCollection);
		TermDocumentMatrix clsTermMarix = new TermDocumentMatrix(clsCollection);
		TermDocumentMatrix reqCodeTermMarix = new TermDocumentMatrix(reqCodeArtifacts);

		System.out.println("[INFO] Computing TF and IDF...");
		TermDocumentMatrix TF = IRUtil.ComputeTF(reqCodeTermMarix);
		double[] IDF = IRUtil.ComputeIDF(IRUtil.ComputeDF(reqCodeTermMarix), reqCodeTermMarix.NumDocs());
		
		//更换不同版本
//		TermDocumentMatrix TF = IRUtil.ComputeTF(clsTermMarix);
//		double[] IDF = IRUtil.ComputeIDF(IRUtil.ComputeDF(clsTermMarix), clsTermMarix.NumDocs());

		for (int i = 0; i < reqTermMarix.NumDocs(); i++) {
			LinksList links = new LinksList();
			String req = reqTermMarix.getDocumentName(i);

			String res = ""; //保存结果
			for (int j = 0; j < clsTermMarix.NumDocs(); j++) {
				String cls = clsTermMarix.getDocumentName(j);
				
				if(0==j%100) {
					System.out.println("[INFO] Computing lambda for class" + cls + ", " + (clsTermMarix.NumDocs()-j) + " left...");
				}

				double lambda = 0.0d;
				WeightUtil weightUtil = new WeightUtil(req, cls);
				// 这里出错
				if (null != codeBitermNumMap.get(cls)) {
					lambda = weightUtil.getLambda3(reqBitermNumMap.get(req).keySet(), codeBitermNumMap.get(cls).keySet(),
							TF, IDF);
					if(Double.compare(lambda, 0.0000001d)>0) {
						res = cls + " " + lambda;
					} else {
						res = cls + " " + 0;
					}
//					System.out.println(res);
					FileIOUtil.continueWriteFile(res+"\n", project.getResultDirPath() + "/"
							+ project.getProjectName()+"_lambdaResult.txt");
				} else {
					System.err.println("出错了 出错了  " + cls);
				}
			}
		}
	}

	public static SimilarityMatrix setCutParameter(SimilarityMatrix matrix_improve) {

		SimilarityMatrix sims = new SimilarityMatrix();

		for (SingleLink link : matrix_improve.allLinks()) {
			if (matrix_improve.isLinkAboveThreshold(link.getSourceArtifactId(), link.getTargetArtifactId())) {
				sims.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
			} else {
				sims.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), 0.0);
			}
		}
		return sims;
	}

}
