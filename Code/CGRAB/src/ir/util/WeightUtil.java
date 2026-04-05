package ir.util;

import document.TermDocumentMatrix;
import experiment.enums.LayerEnum;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WeightUtil {

	public static String REQ;
	public static String CLS;

	public WeightUtil(String req, String cls) {
		this.REQ = req;
		this.CLS = cls;
	}

	public Double getTheta(boolean isUcType, Map<LayerEnum, Map<String, Integer>> reqLayerBiterms,
			Set<String> clsBitermSet, TermDocumentMatrix TF, double[] IDF) {
		double theta = 0.0;

		if (isUcType) {
			Set<String> bitermInSf = reqLayerBiterms.get(LayerEnum.SUB_FLOW).keySet();
			Set<String> bitermInMf = reqLayerBiterms.get(LayerEnum.MAIN_FLOW).keySet();
			Set<String> bitermInAf = reqLayerBiterms.get(LayerEnum.ALT_FLOW).keySet();
			Set<String> bitermInTitle = reqLayerBiterms.get(LayerEnum.TITLE).keySet();

			Set<String> titleCodeSharedBitermSet = genSetIntersect(bitermInTitle, clsBitermSet);
			Set<String> mfCodeSharedBitermSet = genSetIntersect(bitermInMf, clsBitermSet);
			Set<String> sfCodeSharedBitermSet = genSetIntersect(bitermInSf, clsBitermSet);
			Set<String> afCodeSharedBitermSet = genSetIntersect(bitermInAf, clsBitermSet);

			double theta1 = 0.0, theta2 = 0.0, theta3 = 0.0, theta4 = 0.0;

			if (titleCodeSharedBitermSet.size() > 0) {
				double titleBitermIDF = sumOfIDF(IDF, TF, bitermInTitle);
				double titleCodeBitermIDF = sumOfIDF(IDF, TF, titleCodeSharedBitermSet);
				theta1 = (titleCodeBitermIDF) / (titleBitermIDF);
				if (REQ.equals("UC35") && CLS.equals("EmailUtil")) {
					System.out.println("TITLE------------------------");
					titleCodeSharedBitermSet.stream().forEach(s -> {
						System.out.println(s);
					});
					System.out.println("titleBitermIDF: " + titleBitermIDF);
					System.out.println("titleCodeBitermIDF: " + titleCodeBitermIDF);
					System.out.println(theta1);
				}
			}

			if (mfCodeSharedBitermSet.size() > 0) {

				double mfBitermIDF = sumOfIDF(IDF, TF, bitermInMf);
				double mfCodeBitermIDF = sumOfIDF(IDF, TF, mfCodeSharedBitermSet);
				theta2 = (mfCodeBitermIDF) / (mfBitermIDF);
				if (REQ.equals("UC35") && CLS.equals("EmailUtil")) {
					System.out.println("MF------------------------");
					mfCodeSharedBitermSet.stream().forEach(s -> {
						System.out.println(s);
					});
					System.out.println("mfBitermIDF: " + mfBitermIDF);
					System.out.println("mfCodeBitermIDF: " + mfCodeBitermIDF);
					System.out.println(theta2);
				}
			}

			if (sfCodeSharedBitermSet.size() > 0) {
				double sfBitermIDF = sumOfIDF(IDF, TF, bitermInSf);
				double sfCodeBitermIDF = sumOfIDF(IDF, TF, sfCodeSharedBitermSet);
				theta3 = sfCodeBitermIDF / sfBitermIDF;

				if (REQ.equals("UC35") && CLS.equals("EmailUtil")) {
					System.out.println("SF------------------------");
					sfCodeSharedBitermSet.stream().forEach(s -> {
						System.out.println(s);
					});
					System.out.println("sfBitermIDF: " + sfBitermIDF);
					System.out.println("sfCodeBitermIDF: " + sfCodeBitermIDF);
					System.out.println(theta3);
				}
			}

			if (afCodeSharedBitermSet.size() > 0) {
				double afBitermIDF = sumOfIDF(IDF, TF, bitermInAf);
				double afCodeBitermIDF = sumOfIDF(IDF, TF, afCodeSharedBitermSet);
				theta4 = afCodeBitermIDF / afBitermIDF;

				if (REQ.equals("UC35") && CLS.equals("EmailUtil")) {
					System.out.println("AF------------------------");
					sfCodeSharedBitermSet.stream().forEach(s -> {
						System.out.println(s);
					});
					System.out.println("afBitermIDF: " + afBitermIDF);
					System.out.println("afCodeBitermIDF: " + afCodeBitermIDF);
					System.out.println(theta4);
				}
			}

			theta = theta1 * 0.4 + theta2 * 0.3 + theta3 * 0.2 + theta4 * 0.1;

			if (REQ.equals("UC35") && CLS.equals("EmailUtil")) {
				System.out.println("----THETA: " + theta);
			}
		} else {
			Set<String> bitermInSumm = reqLayerBiterms.get(LayerEnum.SUMMARY).keySet();
			Set<String> bitermInDesc = reqLayerBiterms.get(LayerEnum.DESC).keySet();
			Set<String> summCodeSharedBitermSet = genSetIntersect(bitermInSumm, clsBitermSet);
			Set<String> descripCodeSharedBitermSet = genSetIntersect(bitermInDesc, clsBitermSet);

			double theta1 = 0.0, theta2 = 0.0;

			if (summCodeSharedBitermSet.size() > 0) {
				double summBitermIDF = sumOfIDF(IDF, TF, bitermInSumm);
				double summCodeBitermIDF = sumOfIDF(IDF, TF, summCodeSharedBitermSet);
				theta1 = summCodeBitermIDF / summBitermIDF;
			}
			if (descripCodeSharedBitermSet.size() > 0) {
				double descripBitermIDF = sumOfIDF(IDF, TF, bitermInDesc);
				double descripCodeBitermIDF = sumOfIDF(IDF, TF, descripCodeSharedBitermSet);
				theta2 = descripCodeBitermIDF / descripBitermIDF;
			}
			theta += 0.6 * theta1 + 0.4 * theta2;
		}

		if (theta >= 1)
			theta = 0.9999;

		return theta;
	}

	// 好像结果上 3和2一样的，巧合吗？
	public Double getLambda3(Set<String> reqBitermSet, Set<String> clsBitermSet, TermDocumentMatrix TF, double[] IDF) {
//		System.out.println("33333333333333333");
		double lambda = 0.0;
		Set<String> sharedTpSet = genSetIntersect(reqBitermSet, clsBitermSet);
		if (sharedTpSet.size() > 0) {
			// IDF
			double sharedBitermIDF = sumOfIDF(IDF, TF, sharedTpSet);
			double reqBitermIDF = sumOfIDF(IDF, TF, reqBitermSet);
			double codeBitermIDF = sumOfIDF(IDF, TF, clsBitermSet);

			// TF
			double sharedBitermTF = sumOfTF(TF, sharedTpSet);
			double reqBitermTF = sumOfTF(TF, reqBitermSet);
			double codeBitermTF = sumOfTF(TF, clsBitermSet);

			// 类似于 TF * IDF
			lambda = 0.5 * (sharedBitermTF / reqBitermTF) * (sharedBitermIDF / reqBitermIDF)
					+ 0.5 * (sharedBitermTF / codeBitermTF) * (sharedBitermIDF / codeBitermIDF);
		}
		return lambda;
	}

	public Double getLambda2(Set<String> reqBitermSet, Set<String> clsBitermSet, TermDocumentMatrix TF, double[] IDF) {
		System.out.println("2222222222222222222");
		double lambda = 0.0;
		Set<String> sharedTpSet = genSetIntersect(reqBitermSet, clsBitermSet);
		if (sharedTpSet.size() > 0) {
			double sharedBitermIDF = sumOfIDF(IDF, TF, sharedTpSet);
			double reqBitermIDF = sumOfIDF(IDF, TF, reqBitermSet);
			double codeBitermIDF = sumOfIDF(IDF, TF, clsBitermSet);
			// 正在修改中
			double sharedBitermTF = sumOfTF(TF, sharedTpSet);
			double codeBitermTF = sumOfTF(TF, clsBitermSet);

			// 类似于 TF * IDF
			lambda = (sharedBitermTF / codeBitermTF) * (sharedBitermIDF / codeBitermIDF);
		}
		return lambda;
	}

	public Double getLambda(Set<String> reqBitermSet, Set<String> clsBitermSet, TermDocumentMatrix TF, double[] IDF) {
		double lambda = 0.0;

//      System.out.println("getLambda...");
		Set<String> sharedTpSet = genSetIntersect(reqBitermSet, clsBitermSet);
		if (sharedTpSet.size() > 0) {
			double sharedBitermIDF = sumOfIDF(IDF, TF, sharedTpSet);
			double reqBitermIDF = sumOfIDF(IDF, TF, reqBitermSet);
			double codeBitermIDF = sumOfIDF(IDF, TF, clsBitermSet);
			lambda = (0.5 * sharedBitermIDF / reqBitermIDF) + (0.5 * sharedBitermIDF / codeBitermIDF);
//          lambda = sharedBitermIDF / codeBitermIDF;

			if (REQ.equals("UC35") && CLS.equals("EmailUtil")) {
				System.out.println("----LAMBDA: " + lambda);
				System.out.println("----sharedBitermIDF: " + sharedBitermIDF);
				System.out.println("----reqBitermIDF: " + reqBitermIDF);
				System.out.println("----codeBitermIDF: " + codeBitermIDF);
			}
		}
		return lambda;
	}

	public static Double sumOfIDF(double[] IDF, TermDocumentMatrix TF, Set<String> sharedBiterms) {
		double sum = 0.0;
		for (String biterm : sharedBiterms) {
			int index = TF.getTermIndex(biterm);
			sum += IDF[index];
		}
		return sum;
	}

	// 自己加的
	public static Double sumOfTF(TermDocumentMatrix TF, Set<String> sharedBiterms) {
		// shared的和/全部的和
		double sum = 0.0;
		double[][] mx = TF.getMatrix();
		int cIndex = TF.getDocumentIndex(WeightUtil.CLS);
		for (String biterm : sharedBiterms) {
			int termIndex = TF.getTermIndex(biterm);
			sum += mx[cIndex][termIndex];
		}
		return sum;
	}

	public static Set<String> genSetUnion(Set<String> set1, Set<String> set2) {
		Set<String> set = new HashSet<>(set1 == null ? new HashSet<>() : set1);
		set.addAll(set2);
		return set;
	}

	public static Set<String> genSetIntersect(Set<String> set1, Set<String> set2) {
		Set<String> set = new HashSet<>(set1 == null ? new HashSet<>() : set1);
		set.retainAll(set2);
		return set;
	}
}
