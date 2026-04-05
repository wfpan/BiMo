package ir.model;

import document.*;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LSI implements IRModel {
    private TermDocumentMatrix queries;
    private TermDocumentMatrix documents;
    /**k是我们假设的主题数，一般要比文本数少*/
    private static HashMap<String, Integer> projectKvalueMap;

    private int LSI_K;

    public SimilarityMatrix Compute(ArtifactsCollection source, ArtifactsCollection target) {
        initProjectKvalue();
        String projectName = System.getProperty("project");

        LSI_K = projectKvalueMap.get(projectName);

        ArtifactsCollection bothSourceAndTarget = new ArtifactsCollection();
        bothSourceAndTarget.putAll(source);
        bothSourceAndTarget.putAll(target);

        return Compute(new TermDocumentMatrix(source), new TermDocumentMatrix(target),
                new TermDocumentMatrix(bothSourceAndTarget));
    }

    /**@param source req
     * @param target classes
     * @param both req+class
     * */
    private SimilarityMatrix Compute(TermDocumentMatrix source, TermDocumentMatrix target, TermDocumentMatrix both) {

        TermDocumentMatrix TF = ComputeTF(both);
        double[] IDF = ComputeIDF(ComputeDF(both), both.NumDocs());
        TermDocumentMatrix TFIDF_Origin = ComputeTFIDF(TF, IDF);

        TermDocumentMatrix TFIDF_svd = svd(TFIDF_Origin);
        TermDocumentMatrix sourceIDs = ComputeIdentities(source);
        TermDocumentMatrix targetIDs = ComputeIdentities(target);

        TermDocumentMatrix sourceWithTFIDF = ReplaceIDWithTFIDF(sourceIDs, TFIDF_svd);
        TermDocumentMatrix targetWithTFIDF = ReplaceIDWithTFIDF(targetIDs, TFIDF_svd);

        return ComputeSimilarities(sourceWithTFIDF, targetWithTFIDF);
    }

    private TermDocumentMatrix svd(TermDocumentMatrix tfidf_origin) {

        RealMatrix realMatrix = convertTermDocumentMatrixToRealMatrix(tfidf_origin);
        RealMatrix rebuildMatrix = SVD.compute(realMatrix, LSI_K);
        TermDocumentMatrix tfidf_svd = convertRealMatrixToTermDocumentMatrix(rebuildMatrix, tfidf_origin);
        return tfidf_svd;
    }

    /**docIndex*termIndex -> termIndex*docIndex*/
    private RealMatrix convertTermDocumentMatrixToRealMatrix(TermDocumentMatrix tfidf_origin) {
    	//行列互换了一下
        double[][] dates = new double[tfidf_origin.NumTerms()][tfidf_origin.NumDocs()];

        for (int i = 0; i < tfidf_origin.NumTerms(); i++) {
            for (int j = 0; j < tfidf_origin.NumDocs(); j++) {
                dates[i][j] = tfidf_origin.getValue(j, i);
            }
        }

        RealMatrix realMatrix = MatrixUtils.createRealMatrix(dates);
        return realMatrix;
    }

    private TermDocumentMatrix convertRealMatrixToTermDocumentMatrix(RealMatrix rebuildMatrix, TermDocumentMatrix tfidf_origin) {
        for (int i = 0; i < rebuildMatrix.getRowDimension(); i++) {
            for (int j = 0; j < rebuildMatrix.getColumnDimension(); j++) {
                if (rebuildMatrix.getEntry(i, j) > 0.0) {
                    tfidf_origin.setValue(j, i, rebuildMatrix.getEntry(i, j));
                }
            }
        }
        return tfidf_origin;
    }

    private TermDocumentMatrix ReplaceIDWithTFIDF(TermDocumentMatrix ids, TermDocumentMatrix tfidf) {
        for (int i = 0; i < ids.NumDocs(); i++) {
            for (int j = 0; j < ids.NumTerms(); j++) {
                ids.setValue(i, j, tfidf.getValue(ids.getDocumentName(i), ids.getTermName(j)));
            }
        }
        return ids;
    }

    /**w_{x,y} 计算 term x 在 y 文档中的 TF-IDF 值*/
    private TermDocumentMatrix ComputeTFIDF(TermDocumentMatrix tf, double[] idf) {
        for (int i = 0; i < tf.NumDocs(); i++) {
            for (int j = 0; j < tf.NumTerms(); j++) {
                tf.setValue(i, j, tf.getValue(i, j) * idf[j]);
            }
        }
        return tf;
    }

    /**计算每个 term 的 IDF 值*/
    private double[] ComputeIDF(double[] df, int numDocs) {
        double[] idf = new double[df.length];
        for (int i = 0; i < df.length; i++) {
        	if(Double.compare(df[i], 0.0d)<=0) {
        		idf[i] = 0.0d;
        	} else {
                idf[i] = Math.log(numDocs / df[i]);
            }
        	
            /*if (df[i] <= 0.0) { //double比较数据用这种方式不合适，修改成了上面的 wfpan-20230702
                idf[i] = 0.0;
            } else {
                idf[i] = Math.log(numDocs / df[i]);
            }*/
        }
        return idf;
    }

    /**计算一个term在各个文档中出现的次数，也就是包含某一个词的文档树，好像是 IDF的log中的分母*/
    private double[] ComputeDF(TermDocumentMatrix matrix) {
        double[] df = new double[matrix.NumTerms()];
        for (int j = 0; j < matrix.NumTerms(); j++) {
            df[j] = 0.0;
            for (int i = 0; i < matrix.NumDocs(); i++) {
                df[j] += (matrix.getValue(i, j) > 0.0) ? 1.0 : 0.0;
            }
        }
        return df;
    }

    /**计算 TF 值: 词语A在文档j中的次数/文档A中所有词语的次数总和
     * @param matrix term*doc=count的一个matrix
     * @return 返回一个矩阵存储了所有文档中，各个词的 TF*/
    private TermDocumentMatrix ComputeTF(TermDocumentMatrix matrix) {
        for (int i = 0; i < matrix.NumDocs(); i++) {
            double max = 0.0;
            //文档i中所有词语的次数总和
            for (int k = 0; k < matrix.NumTerms(); k++) {
                max += matrix.getValue(i, k);
            }
            //文档i中每个词的次数/文档A中所有词的次数总和
            for (int j = 0; j < matrix.NumTerms(); j++) {
                matrix.setValue(i, j, (matrix.getValue(i, j) / max));
            }
        }
        return matrix;
    }

    /**将 matrix 转为 0-1值，次数大于0为1，其它为 0*/
    private TermDocumentMatrix ComputeIdentities(TermDocumentMatrix matrix) {
        for (int i = 0; i < matrix.NumDocs(); i++) {
            for (int j = 0; j < matrix.NumTerms(); j++) {
                matrix.setValue(i, j, ((matrix.getValue(i, j) > 0.0) ? 1.0 : 0.0));
            }
        }
        return matrix;
    }

    /**计算相似性*/
    private SimilarityMatrix ComputeSimilarities(TermDocumentMatrix ids, TermDocumentMatrix tfidf) {
        SimilarityMatrix sims = new SimilarityMatrix();
        List<TermDocumentMatrix> matrices = TermDocumentMatrix.Equalize(ids, tfidf);

        queries = matrices.get(0);
        documents = matrices.get(1);

        for (int i = 0; i < ids.NumDocs(); i++) {
            LinksList links = new LinksList();
            for (int j = 0; j < tfidf.NumDocs(); j++) {
                double product = 0.0;
                double asquared = 0.0;
                double bsquared = 0.0;
                for (int k = 0; k < matrices.get(0).NumTerms(); k++) {
                    double a = matrices.get(0).getValue(i, k);
                    double b = matrices.get(1).getValue(j, k);
                    product += (a * b);
                    asquared += Math.pow(a, 2);
                    bsquared += Math.pow(b, 2);
                }
                double cross = Math.sqrt(asquared) * Math.sqrt(bsquared);
                
                /*if (cross == 0.0) { //与 0 比较错误 wfpan-2023-07-02
                    links.add(new SingleLink(ids.getDocumentName(i).trim(), tfidf.getDocumentName(j).trim(), 0.0));
                } else {
                    links.add(new SingleLink(ids.getDocumentName(i), tfidf.getDocumentName(j), product / cross));
                }*/
                
                if(0==Double.compare(cross, 0.0d)) {
                	links.add(new SingleLink(ids.getDocumentName(i).trim(), tfidf.getDocumentName(j).trim(), 0.0));
                } else {
                    links.add(new SingleLink(ids.getDocumentName(i), tfidf.getDocumentName(j), product / cross));
                }
                
                
            }

            //降序排序
            Collections.sort(links, Collections.reverseOrder());

            for (SingleLink link : links) {
                sims.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
            }
        }

        return sims;
    }

    /**设置主题数：k是我们假设的主题数，一般要比文本数少，视乎论文中没有提及*/
    public static void initProjectKvalue() {
        projectKvalueMap = new HashMap<>();

        projectKvalueMap.put("itrust", 85);
        projectKvalueMap.put("gantt", 65);
        projectKvalueMap.put("maven", 70);
        projectKvalueMap.put("test", 70);
        projectKvalueMap.put("pig", 140);
        projectKvalueMap.put("infinispan", 170);
        projectKvalueMap.put("drools", 180);
        projectKvalueMap.put("derby", 300);
        projectKvalueMap.put("seam", 130);
        projectKvalueMap.put("groovy", 175);
        
        projectKvalueMap.put("argouml", 70);
    }

    @Override
    public String getModelName() {
        return "LSI";
    }

    @Override
    public TermDocumentMatrix getTermDocumentMatrixOfQueries() {
        return queries;
    }

    @Override
    public TermDocumentMatrix getTermDocumentMatrixOfDocuments() {
        return documents;
    }

}

class SVD {

    public static RealMatrix compute(RealMatrix matrix, int k) {
        return rebuildMatrixBySVD(matrix, k);
    }

    public static RealMatrix rebuildMatrixBySVD(RealMatrix matrix, int k) {

        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        RealMatrix u = svd.getU();
        RealMatrix s = svd.getS();
        RealMatrix v = svd.getV();

        RealMatrix u_k = getFirstKColumns(u, k);
        RealMatrix s_k = getLargestKForS(s, k);
        RealMatrix v_k = getFirstKColumns(v, k);

        RealMatrix result = u_k.multiply(s_k).multiply(v_k.transpose());
        return result;
    }

    public static RealMatrix getLargestKForS(RealMatrix s, int k) {
        return s.getSubMatrix(0, k - 1, 0, k - 1);
    }

    public static RealMatrix getFirstKColumns(RealMatrix u, int k) {
        return u.getSubMatrix(0, u.getRowDimension() - 1, 0, k - 1);
    }

}

