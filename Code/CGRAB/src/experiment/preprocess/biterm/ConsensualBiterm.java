package experiment.preprocess.biterm;

import document.Artifact;
import document.ArtifactsCollection;
import document.TextDataset;
import experiment.enums.CodeEnum;
import experiment.enums.LayerEnum;
import experiment.enums.ProjectEnum;
import experiment.project.Project;

import java.util.*;

public class ConsensualBiterm {
    private ProjectEnum projectEnum;
    private Project project;

    /**Map&lt;String, Map&lt;LayerEnum, Map&lt;String, Integer&gt;&gt;&gt;reqLayerBitermNumMap*/
    private Map<String, Map<LayerEnum, Map<String, Integer>>> reqLayerBitermNumMap;
    /**Map&lt;String, Map&lt;CodeEnum, Map&lt;String, Integer&gt;&gt;&gt;codeLayerBitermsNumMap*/
    private Map<String, Map<CodeEnum, Map<String, Integer>>> codeLayerBitermsNumMap;

    /**req的biterm，删除不存在于code中的那些*/
    private Map<String, Map<String, Integer>> reqConsensualBitermsMap;
    /**code的biterm，删除不存在于req中的那些*/
    private Map<String, Map<String, Integer>> codeConsensualBitermsMap;

    private ExtractCodeBiterm extractCodeBiterm;
    private ExtractReqBiterm extractReqBiterm;

    /**获取了一些数据*/
    public ConsensualBiterm(Project project) {
        this.project = project;
        this.projectEnum = ProjectEnum.getProject(project.getProjectName());
        //构造函数中调用执行
        this.extractCodeBiterm = new ExtractCodeBiterm(project);
        this.extractReqBiterm = new ExtractReqBiterm(project);

        this.codeLayerBitermsNumMap = new HashMap<>();
        this.reqLayerBitermNumMap = new HashMap<>();
        this.reqConsensualBitermsMap = new HashMap<>();
        this.codeConsensualBitermsMap = new HashMap<>();

        extractBiterms();
    }

    /**
     * step1: extract biterms from code<br/>
     * step2: get biterms from req<br/>
     * step3: filter to get consensual biterms
     */

    private void extractBiterms() {
    	//前面构造函数中执行了，所以这里直接调用取值
        codeLayerBitermsNumMap = extractCodeBiterm.getCodeLayerBitermsNumMap();
        reqLayerBitermNumMap = extractReqBiterm.getBitermMap();
        
        reqConsensualBitermsMap = getConsensualReqBitermMap(reqLayerBitermNumMap, extractCodeBiterm.getClsBitermsSet()); // step2
        codeConsensualBitermsMap = getConsensualClassBitermMap(codeLayerBitermsNumMap, extractReqBiterm.getReqBitermsSet()); // step3
        
        //输出biterm信息看看
		/*
		 * Iterator<String> iter = codeConsensualBitermsMap.keySet().iterator();
		 * Iterator<String> tt = null; while(iter.hasNext()) { String xx = iter.next();
		 * tt = codeConsensualBitermsMap.get(xx).keySet().iterator();
		 * while(tt.hasNext()) { String yy = tt.next(); System.out.
		 * println("ConsensualBiterm.extractBiterms[codeConsensualBitermsMap]: " + yy);
		 * } }
		 */
        
    }

    /**Map&lt;String req, Map&lt;LayerEnum description|summary, Map&lt;String biterm, Integer count&gt;&gt;&gt;<br/>
     * bitermNumMap的keyset集合，其实就是所有的biterm的集合<br/>
     * req中有，code中也有，保留。若一个biterm出现多次，次数会相加<br/>
     * req中有，code中没有，删除*/
    private Map<String, Map<String, Integer>> getConsensualReqBitermMap(Map<String, Map<LayerEnum, Map<String, Integer>>> reqLayerBitermNumMap,
                                                                               Set<String> clsBitermSet) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (String req : reqLayerBitermNumMap.keySet()) {
            Map<String, Integer> map = new HashMap<>();
            Map<LayerEnum, Map<String, Integer>> layerBitermMap = reqLayerBitermNumMap.get(req);
            for (LayerEnum layer : layerBitermMap.keySet()) {
                Map<String, Integer> bitermNumMap = layerBitermMap.get(layer);
                Iterator<String> iterator = bitermNumMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String biterm = iterator.next();
                    if (!clsBitermSet.contains(biterm)) {
                        iterator.remove();
                    } else {
                        map.put(biterm, map.getOrDefault(biterm, 0) + bitermNumMap.get(biterm));
                    }
                }
            }
            result.put(req, map);
        }
        return result;
    }

    /**跟上面的过程类似*/
    private Map<String, Map<String, Integer>> getConsensualClassBitermMap(
            Map<String, Map<CodeEnum, Map<String, Integer>>> codeLayerBitermNumMap,
            Set<String> reqBitermSet) {

        Map<String, Map<String, Integer>> result = new HashMap<>();

        for (String cls : codeLayerBitermNumMap.keySet()) {
            Map<String, Integer> map = new HashMap<>();
            Map<CodeEnum, Map<String, Integer>> layerBitermMap = codeLayerBitermNumMap.get(cls);
            for (CodeEnum layer : layerBitermMap.keySet()) {
                Map<String, Integer> bitermNumMap = layerBitermMap.get(layer);
                Iterator<String> iterator = bitermNumMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String biterm = iterator.next();
                    if (!reqBitermSet.contains(biterm)) {
                        iterator.remove();
                    } else {
                        map.put(biterm, map.getOrDefault(biterm, 0) + bitermNumMap.get(biterm));
                    }
                }
            }
            result.put(cls, map);
        }

        return result;
    }

    /**textDataset, reqBitermNumMap, codeBitermNumMap<br/>
     * 根据count增加biterm的次数，从而去更新req和code
     * */
    public TextDataset updateTextDataSet(TextDataset textDataset, Map<String, Map<String, Integer>> reqBitermMap,
                                                Map<String, Map<String, Integer>> codeBitermMap) {
        ArtifactsCollection sourceCollection = textDataset.getSourceCollection();//req
        ArtifactsCollection targetCollection = textDataset.getTargetCollection();//class

        ArtifactsCollection sc = new ArtifactsCollection();
        ArtifactsCollection tc = new ArtifactsCollection();

        //根据count增加出现的次数
        Map<String, String> reqSupMap = getAddString(reqBitermMap);
        Map<String, String> codeSupMap = getAddString_code(codeBitermMap);

        for (String source : sourceCollection.keySet()) {
            Artifact artifact = sourceCollection.get(source);
            String newText;
            if (reqSupMap.containsKey(source)) {
                newText = artifact.text.trim() + " " + reqSupMap.get(source).trim();
            } else {
                newText = artifact.text.trim();
            }
            sc.put(source, new Artifact(artifact.id, newText));
        }

        for (String target : targetCollection.keySet()) {
            Artifact artifact = targetCollection.get(target);
            String newText;
            if (codeSupMap.containsKey(target)) {
                newText = artifact.text.trim() + " " + codeSupMap.get(target).trim();
            } else {
                newText = artifact.text.trim();
            }
            tc.put(target, new Artifact(artifact.id, newText));
        }
        TextDataset newTd = new TextDataset(sc, tc, textDataset.getRtm());
        return newTd;
    }


    /**reqBitermMap根据这里面保存的次数进行重复，然后加入进去*/
    private Map<String, String> getAddString(Map<String, Map<String, Integer>> map) {
        Map<String, String> res = new HashMap<>();
        for (String req : map.keySet()) {
            StringBuilder sb = new StringBuilder();
            Map<String, Integer> tmp = map.get(req);
            for (String biterm : tmp.keySet()) {
                Integer cnt = tmp.get(biterm);
                for (int i = 0; i < cnt; i++) {
                    sb.append(biterm).append(" ");
                }
            }
            res.put(req, sb.toString().trim());
        }
        return res;
    }

    /**codeBitermMap根据这里面保存的次数进行重复，然后加入进去*/
    private Map<String, String> getAddString_code(Map<String, Map<String, Integer>> map) {
        Map<String, String> res = new HashMap<>();
        for (String cls : map.keySet()) {
            StringBuilder sb = new StringBuilder();
            Map<String, Integer> tmp = map.get(cls);
            for (String biterm : tmp.keySet()) {
                Integer cnt = tmp.get(biterm);
                for (int i = 0; i < cnt; i++) {
                    sb.append(biterm).append(" ");
                }
            }
            res.put(cls, sb.toString().trim());
        }
        return res;
    }

    /**req的biterm，删除不存在于code中的那些. &lt;reg, &lt;biterm,count&gt;&gt;*/
    public Map<String, Map<String, Integer>> getReqBitermsMap() {
        return reqConsensualBitermsMap;
    }
    /**code的biterm，删除不存在于req中的那些. &lt;cls,&lt;biterm,count&gt;&gt;*/
    public Map<String, Map<String, Integer>> getCodeBitermsMap() {
        return codeConsensualBitermsMap;
    }
    /**Map&lt;String, Map&lt;LayerEnum, Map&lt;String, Integer&gt;&gt;&gt;reqLayerBitermNumMap*/
    public Map<String, Map<LayerEnum, Map<String, Integer>>> getReqLayerBitermNumMap() {
        return reqLayerBitermNumMap;
    }

    public Map<String, Map<CodeEnum, Map<String, Integer>>> getCodeLayerBitermNumMap() {
        return codeLayerBitermsNumMap;
    }
}
