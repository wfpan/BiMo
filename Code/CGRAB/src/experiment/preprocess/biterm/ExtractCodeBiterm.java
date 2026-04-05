package experiment.preprocess.biterm;

import experiment.enums.CodeEnum;
import experiment.enums.ProjectEnum;
import experiment.preprocess.StanfordNLP;
import experiment.preprocess.TextPreprocess;
import experiment.project.Project;
import util.FileIOUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ExtractCodeBiterm {
	private Project project;
	private StanfordNLP stanfordNlpUtil;

	/**
	 * Map&lt;String类名, Map类内各种元素&lt;CodeEnum, Map&lt;String, Integer&gt;&gt;&gt;
	 */
	private Map<String, Map<CodeEnum, Map<String, Integer>>> codeLayerBitermsNumMap;

	/** bitermNumMap的keyset集合，其实就是所有的biterm的集合 */
	private Set<String> clsBitermsSet;

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		long startTime = System.currentTimeMillis();
		//-------------------------修改1----------------------------------------------//
		//get the full class name of a project such as experiment.project.Test
//		String projectClassName = ProjectEnum.argouml.getName();
		String projectClassName = ProjectEnum.jedit.getName();
		projectClassName = ProjectEnum.tomcat.getName();
		projectClassName = ProjectEnum.Xerces.getName();
		
//		Class projectClass = Class.forName(ProjectEnum.TEST.getName());
//		Class projectClass = Class.forName(ProjectEnum.ant_main.getName());
//		Class projectClass = Class.forName(ProjectEnum.argouml.getName());
//		Class projectClass = Class.forName(ProjectEnum.wro4j.getName());
		Class projectClass = Class.forName(projectClassName);
		
		
		Project project = (Project) projectClass.newInstance();
		
		System.out.println("[INFO] Extracting biterms in source code...");
		ExtractCodeBiterm extractCodeBiterm = new ExtractCodeBiterm(project);

		//-------------------------修改2----------------------------------------------//
//		System.out.println("[INFO] Finish. Please see the results in dataset/"+ProjectEnum.xuml.getName());
		System.out.println("[INFO] Finish. Please see the results in dataset/"+projectClassName);
		long endTime = System.currentTimeMillis();
		System.out.println("time cost (s):" + (endTime - startTime) * 1.0 / 1000);
	}

	/** 输出biterm结果-wfpan 添加的-原来没有的 20230708 */
	public void printResult() {
		System.out.println("[INFO] Clearning the output directory...");
		FileIOUtil.initDirectory(project.getClassDirPath());
		System.out.println("[INFO] Saving the results...");
		Set<String> clsSet = codeLayerBitermsNumMap.keySet();
		Iterator<String> clsIter = clsSet.iterator();

		String cls = "";
		Set<CodeEnum> ceSet = null;
		Iterator<CodeEnum> ceIter = null;
		CodeEnum ce = null;
		Map<String, Integer> bNumMap = null;
		Iterator<String> bitermIter = null;
		String stmp = "";
		TextPreprocess tp = null;
		StringBuilder sb = null;

		while (clsIter.hasNext()) {
			sb = new StringBuilder();
			cls = clsIter.next();
//			System.out.println("类名是" + cls);
			ceSet = codeLayerBitermsNumMap.get(cls).keySet();
			ceIter = ceSet.iterator();
			while (ceIter.hasNext()) {
				ce = ceIter.next();
				bNumMap = codeLayerBitermsNumMap.get(cls).get(ce);
				bitermIter = bNumMap.keySet().iterator();
				while (bitermIter.hasNext()) {
					stmp = bitermIter.next();
					tp = new TextPreprocess(stmp);
					sb.append(tp.doCodeTextProcess()).append(" ");
				}
			}
			FileIOUtil.writeFile(sb.toString().trim(), project.getClassDirPath() + "/" + cls + ".txt");
		}
	}

	/** 用的都是processedPath/code/part下的内容 */
	public ExtractCodeBiterm(Project project) {
		this.project = project;
		this.stanfordNlpUtil = new StanfordNLP();
		this.codeLayerBitermsNumMap = new HashMap<>();
		this.clsBitermsSet = new HashSet<>();
		extractBitermMap();

		// 输出biterm结果 wfpan 添加的，用户保存class内的所有的biterms
		printResult();
	}

	private void extractBitermMap() {
		// processedPath/code/part/class_name
		File dir = new File(project.getClsNameDirPath());
		for (File file : dir.listFiles()) {
			if (!file.getName().contains(".txt"))
				continue;
			String cls = file.getName().split(".txt")[0];
			Map<CodeEnum, Map<String, Integer>> m = genCodeLayerBitermNumMap();
			// 类 vs 类中各个部分的map
			codeLayerBitermsNumMap.put(cls, m);
		}

		//取类名
		File[] clsNameFiles = new File(project.getClsNameDirPath()).listFiles();
		for (File file : clsNameFiles) {
			if (!file.getName().contains(".txt"))
				continue;
			String cls = file.getName().split(".txt")[0]; // 得到类名
			List<List<String>> list = new ArrayList<>();
			Map<String, Integer> bitermNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.CLASS_NAME);

			// 根据路径 //processedPath/code/part/class_name
			List<String> lineList = FileIOUtil.readFile2List(file);
			for (String line : lineList) {
				// 将字符串转为term list
				List<String> termList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
				//考虑了词语间的组合
				List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
				list.addAll(bitermList);

				bitermList.stream().forEach(tp -> {
					String biterm = BitermUtil.getNewTerm(tp);
					// biterm在类名中 count+2，这个结果好像并没有保留下来XXXXX，保存在了 cls 对饮的 bitermNumMap->
					// codeLayerBitermsNumMap,因为是引用类型
					bitermNumMap.put(biterm, bitermNumMap.getOrDefault(biterm, 0) + 2);
				});
			}

			clsBitermsSet.addAll(bitermNumMap.keySet());
		}

		// 取方法名：与clsname类似
		File[] methodNameFiles = new File(project.getMethodNameDirPath()).listFiles();
		for (File file : methodNameFiles) {
			if (!file.getName().contains(".txt"))
				continue;
			String cls = file.getName().split(".txt")[0];
			List<List<String>> list = new ArrayList<>();
			Map<String, Integer> bitermNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.METHOD_NAME);
			List<String> lineList = FileIOUtil.readFile2List(file);
			for (String line : lineList) {
				List<String> termList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
				//考虑了词语间的组合
				List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
				list.addAll(bitermList);
				bitermList.stream().forEach(bl -> {
					String biterm = BitermUtil.getNewTerm(bl);
					bitermNumMap.put(biterm, bitermNumMap.getOrDefault(biterm, 0) + 2);
				});
			}
			clsBitermsSet.addAll(bitermNumMap.keySet());
		}
		expandBiterm();
	}

	/**
	 * expend biterm from param name, param type, field name, field type, invoke
	 * method, comment
	 */
	private void expandBiterm() {
		File[] invokeFiles = new File(project.getInvokeMethodDirPath()).listFiles();
		Map<String, List<String>> clsCallMap = new HashMap<>();
		for (File f : invokeFiles) {
			String cls = f.getName().split("\\.")[0];
			clsCallMap.put(cls, FileIOUtil.readFile2List(f));
		}

		File[] fieldNameFiles = new File(project.getFieldNameDirPath()).listFiles();
		Map<String, List<String>> clsFieldNameMap = new HashMap<>();
		for (File f : fieldNameFiles) {
			String cls = f.getName().split("\\.")[0];
			clsFieldNameMap.put(cls, FileIOUtil.readFile2List(f));
		}

		File[] fieldTypeFiles = new File(project.getFieldTypeDirPath()).listFiles();
		Map<String, List<String>> clsFieldTypeMap = new HashMap<>();
		for (File f : fieldTypeFiles) {
			String cls = f.getName().split("\\.")[0];
			clsFieldTypeMap.put(cls, FileIOUtil.readFile2List(f));
		}

		File[] paramNameFiles = new File(project.getParamNameDirPath()).listFiles();
		Map<String, List<String>> clsParamNameMap = new HashMap<>();
		for (File f : paramNameFiles) {
			String cls = f.getName().split("\\.")[0];
			clsParamNameMap.put(cls, FileIOUtil.readFile2List(f));
		}

		File[] paramTypeFiles = new File(project.getParamTypeDirPath()).listFiles();
		Map<String, List<String>> clsParamTypeMap = new HashMap<>();
		for (File f : paramTypeFiles) {
			String cls = f.getName().split("\\.")[0];
			clsParamTypeMap.put(cls, FileIOUtil.readFile2List(f));
		}

		Map<String, List<List<String>>> commentBitermMap = new HashMap<>();
		File dir = new File(project.getCommentDirPath());
		for (File file : dir.listFiles()) {
			if (!file.getName().contains(".txt"))
				continue;
			String cls = file.getName().split(".txt")[0];
			List<List<String>> l = new ArrayList<>();
			commentBitermMap.put(cls, l);
			List<String> bitermList = FileIOUtil.readFile2List(file);
			if (bitermList.size() > 0) {
				for (String tp : bitermList) {
					List<String> list = new LinkedList<>();
					String relation = tp.split(":")[1];
					tp = tp.split(":")[0];
					if (tp.split(" ").length > 2)
						continue;
					list.add(tp.split(" ")[0]);
					list.add(tp.split(" ")[1]);
					l.add(list);
				}
			}
		}

		int addNum = 1;
		for (String cls : codeLayerBitermsNumMap.keySet()) {
			if (cls.contains("jsp"))
				continue;

			// extract from invoked method name
			List<String> callList = clsCallMap.get(cls);
			Map<String, Integer> invokeNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.INVOKED_METHOD);
			if (callList != null && callList.size() != 0) {
				for (String call : callList) {
					List<String> termList = Arrays.stream(call.split(" ")).collect(Collectors.toList());
					List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
					bitermList.stream().forEach(tp -> {
						String biterm = BitermUtil.getNewTerm(tp);
						invokeNumMap.put(biterm, invokeNumMap.getOrDefault(biterm, addNum));
					});
					clsBitermsSet.addAll(invokeNumMap.keySet());
				}
			}

			// extract from field name
			List<String> fieldNameList = clsFieldNameMap.get(cls);
			Map<String, Integer> fieldNameNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.FIELD_NAME);
			if (fieldNameList != null && fieldNameList.size() != 0) {
				for (String fieldName : fieldNameList) {
					List<String> termList = Arrays.stream(fieldName.split(" ")).collect(Collectors.toList());
					List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
					bitermList.stream().forEach(tp -> {
						String biterm = BitermUtil.getNewTerm(tp);
						fieldNameNumMap.put(biterm, fieldNameNumMap.getOrDefault(biterm, addNum));
					});
					clsBitermsSet.addAll(fieldNameNumMap.keySet());
				}
			}

			// extract from field type
			List<String> fieldTypeList = clsFieldTypeMap.get(cls);
			Map<String, Integer> fieldTypeNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.FIELD_TYPE);
			if (fieldTypeList != null && fieldTypeList.size() != 0) {
				for (String fieldType : fieldTypeList) {
					List<String> termList = Arrays.stream(fieldType.split(" ")).collect(Collectors.toList());
					List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
					bitermList.stream().forEach(tp -> {
						String biterm = BitermUtil.getNewTerm(tp);
						fieldTypeNumMap.put(biterm, fieldTypeNumMap.getOrDefault(biterm, addNum));
					});
					clsBitermsSet.addAll(fieldTypeNumMap.keySet());
				}
			}

			// extract from param name
			List<String> paramNameList = clsParamNameMap.get(cls);
			Map<String, Integer> paramNameNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.PARAM_NAME);
			if (paramNameList != null && paramNameList.size() != 0) {
				for (String paramName : paramNameList) {
					List<String> termList = Arrays.stream(paramName.split(" ")).collect(Collectors.toList());
					List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
					bitermList.stream().forEach(tp -> {
						String biterm = BitermUtil.getNewTerm(tp);
						paramNameNumMap.put(biterm, paramNameNumMap.getOrDefault(biterm, addNum));
					});
					clsBitermsSet.addAll(paramNameNumMap.keySet());
				}
			}

			// extract from param type
			List<String> paramTypeList = clsFieldTypeMap.get(cls);
			Map<String, Integer> paramTypeNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.PARAM_TYPE);
			if (paramTypeList != null && paramTypeList.size() != 0) {
				for (String paramType : paramTypeList) {
					List<String> termList = Arrays.stream(paramType.split(" ")).collect(Collectors.toList());
					List<List<String>> bitermList = BitermUtil.combineTwoTerm(termList);
					bitermList.stream().forEach(tp -> {
						String biterm = BitermUtil.getNewTerm(tp);
						paramTypeNumMap.put(biterm, paramTypeNumMap.getOrDefault(biterm, addNum));
					});
					clsBitermsSet.addAll(paramTypeNumMap.keySet());
				}
			}

			// extract from comment
			Map<String, Integer> commentNumMap = codeLayerBitermsNumMap.get(cls).get(CodeEnum.COMMENT);
			if (commentBitermMap.get(cls) != null) {
				List<List<String>> bitermList = commentBitermMap.get(cls);
				bitermList.stream().forEach(tp -> {
					String biterm = BitermUtil.getNewTerm(tp);
					commentNumMap.put(biterm, commentNumMap.getOrDefault(biterm, 0) + 1);
				});
				clsBitermsSet.addAll(commentNumMap.keySet());
			}
		}
	}

	/** 一个Map, Map&lt;CodeEnum类型, Map&lt;String, Integer&gt;&gt; */
	private static Map<CodeEnum, Map<String, Integer>> genCodeLayerBitermNumMap() {
		Map<CodeEnum, Map<String, Integer>> map = new HashMap<>();
		map.put(CodeEnum.CLASS_NAME, new HashMap<String, Integer>());
		map.put(CodeEnum.METHOD_NAME, new HashMap<String, Integer>());
		map.put(CodeEnum.INVOKED_METHOD, new HashMap<String, Integer>());
		map.put(CodeEnum.COMMENT, new HashMap<String, Integer>());
		map.put(CodeEnum.FIELD_NAME, new HashMap<String, Integer>());
		map.put(CodeEnum.FIELD_TYPE, new HashMap<String, Integer>());
		map.put(CodeEnum.PARAM_NAME, new HashMap<String, Integer>());
		map.put(CodeEnum.PARAM_TYPE, new HashMap<String, Integer>());
		return map;
	}

	/**
	 * 返回一个Map&lt;String类名, Map类内各种元素&lt;CodeEnum, Map&lt;String,
	 * Integer&gt;&gt;&gt;
	 */
	public Map<String, Map<CodeEnum, Map<String, Integer>>> getCodeLayerBitermsNumMap() {
		return codeLayerBitermsNumMap;
	}

	/** return bitermNumMap的keyset集合，其实就是所有的biterm的集合 */
	public Set<String> getClsBitermsSet() {
		return clsBitermsSet;
	}
}
