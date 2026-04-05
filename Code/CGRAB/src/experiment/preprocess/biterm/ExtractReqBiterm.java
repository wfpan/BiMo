package experiment.preprocess.biterm;

import edu.stanford.nlp.util.CoreMap;
import experiment.enums.LayerEnum;
import experiment.enums.ProjectEnum;
import experiment.preprocess.StanfordNLP;
import experiment.project.Project;
import util.FileIOUtil;

import java.io.File;
import java.util.*;

public class ExtractReqBiterm {
	private Project project;
	private ProjectEnum projectEnum;
	private StanfordNLP stanfordNlpUtil;
	/**
	 * Map&lt;String req, Map&lt;LayerEnum description|summary, Map&lt;String
	 * biterm, Integer count&gt;&gt;&gt;
	 */
	private Map<String, Map<LayerEnum, Map<String, Integer>>> reqLayerBitermNumMap;
	/** req中biterm的集合 */
	private Set<String> reqBitermsSet;

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		long startTime = System.currentTimeMillis();
//		ProjectEnum projectEnum = ProjectEnum.TEST; // select project
//		ProjectEnum projectEnum = ProjectEnum.ant_main;
//		ProjectEnum projectEnum = ProjectEnum.argouml;
//		ProjectEnum projectEnum = ProjectEnum.wro4j;
		String projectClassName = ProjectEnum.argouml.getName(); //修改1：修改此处
		projectClassName = ProjectEnum.Xerces.getName(); //修改1：修改此处
		projectClassName = ProjectEnum.Maze.getName(); //修改1：修改此处
		
//		Class projectClass = Class.forName(projectEnum.getName());
		Class projectClass = Class.forName(projectClassName);
		
		Project project = (Project) projectClass.newInstance();
		ExtractReqBiterm extractReqBiterm = new ExtractReqBiterm(project);

		extractReqBiterm.extractDescBiterm();
		System.out.println("[INFO] Finish.");
		/*
		 * if (projectEnum.equals(ProjectEnum.ITRUST) ||
		 * projectEnum.equals(ProjectEnum.GANTT)) { extractReqBiterm.extractUcBiterm();
		 * } else { extractReqBiterm.extractIssueBiterm(); }
		 */
		long endTime = System.currentTimeMillis();
		System.out.println("time cost (s):" + (endTime - startTime) * 1.0 / 1000);
	}

	public ExtractReqBiterm(Project project) {
		this.project = project;
		this.projectEnum = ProjectEnum.getProject(project.getProjectName());
		this.stanfordNlpUtil = new StanfordNLP();
	}

	/**
	 * @author wfpan <br/>
	 * 2023/07/08<br/>
	 * 提取description中的biterms*/
	private void extractDescBiterm() {
		// 清理这些目录:因为后面是在文件中持续写入的，不清除多次运行的结果就叠加了
		System.out.println("[INFO] Clearning the output directories....");
		FileIOUtil.initDirectory(project.getIssueDescripBitermDirPath());
		FileIOUtil.initDirectory(project.getReqPath());
		extractBiterm2(project.getUnprocessedIssueDescripDirPath(), project.getIssueDescripBitermDirPath());
	}

	private void extractIssueBiterm() {
		// 清理这些目录:因为后面是在文件中持续写入的，不清除多次运行的结果就叠加了
		FileIOUtil.initDirectory(project.getIssueDescripBitermDirPath());
		FileIOUtil.initDirectory(project.getIssueSummBitermDirPath());

		extractBiterm(project.getUnprocessedIssueDescripDirPath(), project.getIssueDescripBitermDirPath());
		extractBiterm(project.getUnprocessedIssueSummDirPath(), project.getIssueSummBitermDirPath());
	}

	private void extractUcBiterm() {
		FileIOUtil.initDirectory(project.getUcTitleBitermDirPath());
		FileIOUtil.initDirectory(project.getUcPreconBitermDirPath());
		FileIOUtil.initDirectory(project.getUcMainflowBitermDirPath());
		FileIOUtil.initDirectory(project.getUcSubflowBitermDirPath());
		FileIOUtil.initDirectory(project.getUcAlterflowBitermDirPath());

		extractBiterm(project.getUnprocessedUcTitleDirPath(), project.getUcTitleBitermDirPath());
		extractBiterm(project.getUnprocessedUcPreconDirPath(), project.getUcPreconBitermDirPath());
		extractBiterm(project.getUnprocessedUcMainflowDirPath(), project.getUcMainflowBitermDirPath());
		extractBiterm(project.getUnprocessedUcSubflowDirPath(), project.getUcSubflowBitermDirPath());
		extractBiterm(project.getUnprocessedUcAlterflowDirPath(), project.getUcAlterflowBitermDirPath());
	}
	
	/**
	 * @author wfpan<br/>
	 * 2023/07/08<br/>
	 * ex: unprocessed/req/description ->
	 * processedPath/req/part/description 以及 processed/req
	 */
	private void extractBiterm2(String unprocessedPartDir, String outputDir) {
		System.out.println("[INFO] Extracting biterms from documentation and saving the results...");
		File unprocessedPart = new File(unprocessedPartDir);
		
		int fCnt = 0;
		
		if (unprocessedPart.isDirectory()) {
			for (File f : unprocessedPart.listFiles()) {
				if (!f.getName().contains(".txt")) {
					continue;
				}
				System.out.println("Processing the " + fCnt++ +"/"+unprocessedPart.listFiles().length + " file...");
//				System.out.println(f.getName());
				List<String> issueTxtLineList = FileIOUtil.readFileByLine(f.getPath());
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < issueTxtLineList.size(); i++) {
					String currentLine = issueTxtLineList.get(i);
					if (!currentLine.equals("")) {
						List<CoreMap> sentenceList = stanfordNlpUtil.splitSentence(currentLine);
						sentenceList.stream().forEach(s -> {
							String termPairs = stanfordNlpUtil.getTermPair(s.toString());
							if (!termPairs.equals("")) {
								FileIOUtil.continueWriteFile(termPairs, outputDir + "/" + f.getName());
								
								//保存一份到processed/req/ 两个方向都加入
								String relation = termPairs.split(":")[1];
								termPairs = termPairs.split(":")[0];
								if (2==termPairs.split(" ").length) {
									
									/*
									 * List<String> list1 = new LinkedList<>(); list1.add(termPairs.split(" ")[0]);
									 * list1.add(termPairs.split(" ")[1]); String biterm1 =
									 * BitermUtil.getNewTerm(list1); sb.append(biterm1).append(" ");
									 */
									sb.append(termPairs.split(" ")[0]).append(" ").append(termPairs.split(" ")[1]).append(" ");
//									sb.append(termPairs.split(" ")[1]).append(" ").append(termPairs.split(" ")[0]).append(" ");
//									System.out.println("newterm="+biterm1);
//									System.exit(0);
									
									/*
									 * List<String> list2 = new LinkedList<>(); list2.add(termPairs.split(" ")[1]);
									 * list2.add(termPairs.split(" ")[0]); String biterm2 =
									 * BitermUtil.getNewTerm(list2); sb.append(biterm2).append(" ");
									 */
								}
							}
							
						});
					}
				}//for
				//保存到processed/req/
				FileIOUtil.continueWriteFile(sb.toString().trim(), project.getReqPath() + "/" + f.getName()); //wfpan
			}//for-file
		}
	}

	/**
	 * ex: unprocessed/req/description or summary ->
	 * processedPath/req/part/description or summary
	 */
	private void extractBiterm(String unprocessedPartDir, String outputDir) {
		File unprocessedPart = new File(unprocessedPartDir);
		
		if (unprocessedPart.isDirectory()) {
			for (File f : unprocessedPart.listFiles()) {
				if (!f.getName().contains(".txt")) {
					continue;
				}
				System.out.println(f.getName());
				List<String> issueTxtLineList = FileIOUtil.readFileByLine(f.getPath());
				for (int i = 0; i < issueTxtLineList.size(); i++) {
					String currentLine = issueTxtLineList.get(i);
					if (!currentLine.equals("")) {
						List<CoreMap> sentenceList = stanfordNlpUtil.splitSentence(currentLine);
						sentenceList.stream().forEach(s -> {
							String termPairs = stanfordNlpUtil.getTermPair(s.toString());
							if (!termPairs.equals("")) {
								FileIOUtil.continueWriteFile(termPairs, outputDir + "/" + f.getName());
							}
						});
					}
				}//for
			}//for-file
		}
	}

	/**
	 * 返回Map&lt;String req, Map&lt;LayerEnum description|summary, Map&lt;String
	 * biterm, Integer count&gt;&gt;&gt;
	 */
	public Map<String, Map<LayerEnum, Map<String, Integer>>> getBitermMap() {
		reqBitermsSet = new HashSet<>();
		reqLayerBitermNumMap = new HashMap<>();

		// [WARNING] 注意：这里视乎只是使用了文件名，没有使用其中的内容
		//File dir = new File(project.getReqPath()); //原来的 wfpan
		File dir = new File(project.getUnprocessedIssueDescripDirPath()); //修改为这个 wfpan 这个目录是肯定存在的

		for (File file : dir.listFiles()) {
			if (!file.getName().contains(".txt"))
				continue;
			String req = file.getName().split(".txt")[0];
			Map<String, Integer> tmp = new HashMap<>();
			Map<LayerEnum, Map<String, Integer>> m = genLayerBitermNumMap();
			reqLayerBitermNumMap.put(req, m);
		}

		if (projectEnum.getName().equals(ProjectEnum.TEST.getName())
				|| projectEnum.getName().equals(ProjectEnum.MAVEN.getName())
				|| projectEnum.getName().equals(ProjectEnum.SEAM.getName())
				|| projectEnum.getName().equals(ProjectEnum.GROOVY.getName())
				|| projectEnum.getName().equals(ProjectEnum.PIG.getName())
				|| projectEnum.getName().equals(ProjectEnum.DERBY.getName())
				|| projectEnum.getName().equals(ProjectEnum.INFINISPAN.getName())
				|| projectEnum.getName().equals(ProjectEnum.DROOLS.getName())
				//-----------------------------------------------------------------
				|| projectEnum.getName().equals(ProjectEnum.ant_main.getName())
				|| projectEnum.getName().equals(ProjectEnum.jedit.getName())
				|| projectEnum.getName().equals(ProjectEnum.jhotdraw.getName())
				|| projectEnum.getName().equals(ProjectEnum.jmeter_core.getName())
				|| projectEnum.getName().equals(ProjectEnum.wro4j.getName())
				|| projectEnum.getName().equals(ProjectEnum.gwtportlets.getName())
				|| projectEnum.getName().equals(ProjectEnum.javaclient.getName())
				|| projectEnum.getName().equals(ProjectEnum.jgap.getName())
				|| projectEnum.getName().equals(ProjectEnum.Mars.getName())
				|| projectEnum.getName().equals(ProjectEnum.Maze.getName())
				|| projectEnum.getName().equals(ProjectEnum.neuroph.getName())
				|| projectEnum.getName().equals(ProjectEnum.tomcat.getName())
				|| projectEnum.getName().equals(ProjectEnum.JPMC.getName())
				|| projectEnum.getName().equals(ProjectEnum.log4j.getName())
				|| projectEnum.getName().equals(ProjectEnum.PDFBox.getName())
				|| projectEnum.getName().equals(ProjectEnum.Xerces.getName())
				|| projectEnum.getName().equals(ProjectEnum.xuml.getName())
				|| projectEnum.getName().equals(ProjectEnum.argouml.getName())) {
			// 使用的是处理后的部分：processedPath/req_biterm/summary
//			readLayerBiterm(LayerEnum.SUMMARY, project.getIssueSummBitermDirPath());
			readLayerBiterm(LayerEnum.DESC, project.getIssueDescripBitermDirPath()); //只有desc，没有summary
		} else {
			readLayerBiterm(LayerEnum.TITLE, project.getUcTitleBitermDirPath());
			readLayerBiterm(LayerEnum.PRE_CON, project.getUcPreconBitermDirPath());
			readLayerBiterm(LayerEnum.MAIN_FLOW, project.getUcMainflowBitermDirPath());
			readLayerBiterm(LayerEnum.SUB_FLOW, project.getUcSubflowBitermDirPath());
			readLayerBiterm(LayerEnum.ALT_FLOW, project.getUcAlterflowBitermDirPath());
		}
		return reqLayerBitermNumMap;
	}

	/**
	 * 使用的是处理后的部分：processedPath/req_biterm/summary or description<br/>
	 * 两个方向都加入,AB,BA都加入的
	 */
	private void readLayerBiterm(LayerEnum layer, String bitermDirPath) {
		File dir = new File(bitermDirPath);
		for (File file : dir.listFiles()) {
			// 获取各需求文件的名字req1.txt -> req1
			if (!file.getName().contains(".txt"))
				continue;
			String req = file.getName().split(".txt")[0];
			Map<String, Integer> layerBitermNumMap = reqLayerBitermNumMap.get(req).get(layer);
			List<String> bitermList = FileIOUtil.readFile2List(file);

			if (bitermList.size() > 0) {
				for (String biterm : bitermList) {
					String relation = biterm.split(":")[1];
					biterm = biterm.split(":")[0];

					int addNum = 1;
					List<String> list = new LinkedList<>();
					list.add(biterm.split(" ")[0]);
					list.add(biterm.split(" ")[1]);
					String biterm1 = BitermUtil.getNewTerm(list);
					if (layer.getName().equals(LayerEnum.TITLE.getName())
							|| layer.getName().equals(LayerEnum.SUMMARY.getName())) {
						addNum = 1;
					}
					layerBitermNumMap.put(biterm1, layerBitermNumMap.getOrDefault(biterm1, 0) + addNum);

					List<String> list2 = new LinkedList<>();
					list2.add(biterm.split(" ")[1]);
					list2.add(biterm.split(" ")[0]);
					String biterm2 = BitermUtil.getNewTerm(list2);
					if (layer.getName().equals(LayerEnum.TITLE.getName())
							|| layer.getName().equals(LayerEnum.SUMMARY.getName())) {
						addNum = 1;
					}
					layerBitermNumMap.put(biterm2, layerBitermNumMap.getOrDefault(biterm2, 0) + addNum);
				}
			} // if
			reqBitermsSet.addAll(layerBitermNumMap.keySet());
		} // for
	}

	private static Map<LayerEnum, Map<String, Integer>> genLayerBitermNumMap() {
		Map<LayerEnum, Map<String, Integer>> map = new HashMap<>();
		map.put(LayerEnum.TITLE, new HashMap<String, Integer>());
		map.put(LayerEnum.PRE_CON, new HashMap<String, Integer>());
		map.put(LayerEnum.MAIN_FLOW, new HashMap<String, Integer>());
		map.put(LayerEnum.SUB_FLOW, new HashMap<String, Integer>());
		map.put(LayerEnum.ALT_FLOW, new HashMap<String, Integer>());
		map.put(LayerEnum.UC_TEXT, new HashMap<String, Integer>());
		map.put(LayerEnum.SUMMARY, new HashMap<String, Integer>());
		map.put(LayerEnum.DESC, new HashMap<String, Integer>());
		return map;
	}

	/** return req中biterm的集合 */
	public Set<String> getReqBitermsSet() {
		return reqBitermsSet;
	}
}
