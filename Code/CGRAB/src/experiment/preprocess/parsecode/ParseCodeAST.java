package experiment.preprocess.parsecode;

import edu.stanford.nlp.util.CoreMap;
import experiment.enums.CodeEnum;
import experiment.preprocess.StanfordNLP;
import experiment.preprocess.TextPreprocess;
import experiment.preprocess.parsecode.entity.ClassEntity;
import experiment.preprocess.parsecode.entity.FieldEntity;
import experiment.preprocess.parsecode.entity.MethodEntity;
import experiment.preprocess.parsecode.entity.ParameterEntity;
import experiment.preprocess.parsecode.util.ClassASTVisitor;
import experiment.preprocess.parsecode.util.JavaToXmlUtil;
import experiment.preprocess.parsecode.util.JdtAstUtil;
import experiment.project.ProjectConfig;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import util.FileIOUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/** 根据unprocessed\code中的代码，提取出ast_xml, part以及processed\code\part中的各个部分 */
public class ParseCodeAST {

	private String projectName;
	private Set<String> rtmClsSet;
	private ProjectConfig config;
	/** 类实体列表 */
	private List<ClassEntity> classEntityList;
	private Map<String, List<Comment>> clsBlockCommentMap;

	private Set<String> allClsSet;
	private Set<String> allMethodSet;

	/** 配置各种路径、从RTM得到类名 */
	public ParseCodeAST(String projectName) {
		this.projectName = projectName;
		this.config = new ProjectConfig(projectName);
		this.rtmClsSet = FileIOUtil.getClassNameFromRTM(config.rtmClassPath);
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String prj = "Xerces";
		
//		ParseCodeAST parseCodeAST = new ParseCodeAST("ant_main");
//		ParseCodeAST parseCodeAST = new ParseCodeAST("ant_main");
//		parseCodeAST = new ParseCodeAST("argouml");
//		parseCodeAST = new ParseCodeAST("jedit");
		
		ParseCodeAST parseCodeAST = new ParseCodeAST(prj);
		
		parseCodeAST.parseCode();

		parseCodeAST.writeClassInfo();
		long endTime = System.currentTimeMillis();
		System.out.println("finish.");
		System.out.println("time cost (s):" + (endTime - startTime) * 1.0 / 1000);
	}

	/** 根据路径得到类的全名.java, ex: dataset\\test\\unprocessed\\code\\src\\org\\apache\\tools\\ant\\AntClassLoader.java<br/>
	 *  -> org.apthe.tools.and.AntClassLoader.java */
	public String pathToClassName(String path) {
		String classFullName = null;
		classFullName = path.replace("dataset\\"+this.projectName+"\\unprocessed\\code\\src\\", "").replace("\\", ".");
		return classFullName;
	}

	/** convert the Java source code file to AST, output the xml file */
	private void parseCode() {
		classEntityList = new ArrayList<>();
		clsBlockCommentMap = new HashMap<>();
		FileIOUtil.initDirectory(config.astXmlPath);

		/** 类个数的计数 */
		int classId = 0;
		ArrayList<File> fileList = FileIOUtil.getFileList(config.srcPath, "java");
		System.out.println("[Info] Directory=" + config.srcPath + ", #files=" + fileList.size());
		System.out.println("[Info] Building AST files for all .java file...");

		String classFullName = null;

		for (File file : fileList) {
			System.out.println("[Info] Processing ["+file.getName()+"], "+ (fileList.size()-(classId+1))+"/"+fileList.size() +" left");
//			System.out.println(file.getName());
//			System.out.println("getPath:" + file.getPath());

			classFullName = pathToClassName(file.getPath());
			String clsName = classFullName.substring(0, classFullName.lastIndexOf(".")); //wfpan
//			String clsName = file.getName().split("\\.")[0]; //原来的
//			System.out.println(classFullName + "->" + clsName);
//			System.exit(0);

			CompilationUnit comp = JdtAstUtil.getCompilationUnit(file.getPath());
			// 类全名
			clsBlockCommentMap.put(clsName, comp.getCommentList());

			ClassASTVisitor visitor = new ClassASTVisitor();
			comp.accept(visitor);
			visitor.getClassEntity().setId(classId);
			visitor.getClassEntity().setField(visitor.getFieldList());
			visitor.getClassEntity().setMethod(visitor.getMethodList());

			ClassEntity classEntity = visitor.getClassEntity();
			classEntityList.add(classEntity);
			String xml = JavaToXmlUtil.beanToXml(classEntity);

			// 因为一个java文件存在定义多个类的情况，导致文件名与从xml文件解析到的类名不一致，导致后面空指针问题。因此这里进行修正 wfpan
			String newClassName = classEntity.getPackageName() + "." + classEntity.getClassName();
			if (!newClassName.equals(clsName)) {
				System.err
						.println("[Error] 解析到的类名为 [" + newClassName + "] 与文件名解析到的类名 " + "[" + clsName + "] 不符，修正类名");
				clsBlockCommentMap.put(newClassName, comp.getCommentList());
			}

			classId++;

//			String fileName = file.getName(); //原来的 wfpan注释掉
			String fileName = classFullName; // wfpan修正后的
			String fileMainName = fileName.substring(0, fileName.lastIndexOf("."));

			File outputFile = new File(config.astXmlPath, fileMainName + ".xml");
			// 结果保存到xml文件中
			try {
				FileWriter fileWriter = new FileWriter(outputFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(xml);
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/** 提取类名allClsSet及各个类下的方法名allMethodSet */
	private void readAllMethod() {
		allClsSet = new HashSet<>();
		allMethodSet = new HashSet<>();
		for (ClassEntity cls : classEntityList) {
			allClsSet.add(cls.getClassName());
			List<MethodEntity> methodEntities = cls.getMethod();
			if (methodEntities != null) {
				for (MethodEntity method : methodEntities) {
					allMethodSet.add(method.getMethodName());
				}
			}
		}
	}

	private void writeClassInfo() {
		// 准备code/part下的各个目录
		config.initCodePartDir();
		readAllMethod();
		TextPreprocess preprocess;
		
		int cCnt = 1; //计数wfpan
		System.out.println("\n[Info] Storing class information: class|method|field|param|invokedMethod-name, field|param type, class|method-comment...");
		
		for (ClassEntity cls : classEntityList) {
			System.out.println("[Info] Processing ["+cls.getClassName()+"], "+ (classEntityList.size()-cCnt++)+"/"+classEntityList.size() +" left");

			// 判断一个类是不是RTM中提到的类（有连接关系的）。如果类不在那个RTM中，则跳过。[错误的]
			/*
			 * if (!rtmClsSet.contains(cls.getClassName())) continue;
			 */

			StringBuilder fieldNameSb = new StringBuilder();
			StringBuilder fieldTypeSb = new StringBuilder();
			StringBuilder methodNameSb = new StringBuilder();
			StringBuilder commentSb = new StringBuilder();
			StringBuilder paramNameSb = new StringBuilder();
			StringBuilder paramTypeSb = new StringBuilder();
			StringBuilder invokeMethodNameSb = new StringBuilder();

			StringBuilder processedFieldNameSb = new StringBuilder();
			StringBuilder processedFieldTypeSb = new StringBuilder();
			StringBuilder processedMethodNameSb = new StringBuilder();
			StringBuilder processedParamNameSb = new StringBuilder();
			StringBuilder processedParamTypeSb = new StringBuilder();
			StringBuilder processedInvokeMethodNameSb = new StringBuilder();

			List<MethodEntity> methodEntities = cls.getMethod();
			if (methodEntities != null) {
				for (MethodEntity method : methodEntities) {
					methodNameSb.append(method.getMethodName() + "\n");
					preprocess = new TextPreprocess(method.getMethodName());
					processedMethodNameSb.append(preprocess.doCodeTextProcess() + "\n");

					if (method.getInnerMethodInvokeList() != null) {
						for (String invokeMethod : method.getInnerMethodInvokeList()) {
							if (invokeMethod != "") {
								// System.out.println(invokeMethod);
								invokeMethod = invokeMethod.split(";")[1];
								if (allMethodSet.contains(invokeMethod)) {
									invokeMethodNameSb.append(invokeMethod + "\n");
									preprocess = new TextPreprocess(invokeMethod);
									processedInvokeMethodNameSb.append(preprocess.doCodeTextProcess() + "\n");
								}
							}
						}
					}
					List<ParameterEntity> paramEntities = method.getParametersList();
					for (ParameterEntity param : paramEntities) {
						if (param != null) {
							paramNameSb.append(param.getParamName() + "\n");
							preprocess = new TextPreprocess(param.getParamName());
							processedParamNameSb.append(preprocess.doCodeTextProcess() + "\n");
							if (rtmClsSet.contains(param.getParamType())) {
								paramTypeSb.append(param.getParamType() + "\n");
								preprocess = new TextPreprocess(param.getParamType());
								processedParamTypeSb.append(preprocess.doCodeTextProcess() + "\n");
							}
						}
					}
				}
			}

			List<FieldEntity> fieldEntities = cls.getField();
			// System.out.println(cls.getClassName() + ":" + fieldEntities.size());

			if (fieldEntities != null) {
				for (FieldEntity field : fieldEntities) {
					fieldNameSb.append(field.getFieldName() + "\n");
					preprocess = new TextPreprocess(field.getFieldName());
					processedFieldNameSb.append(preprocess.doCodeTextProcess() + "\n");
					if (rtmClsSet.contains(field.getFieldType())) {
						fieldTypeSb.append(field.getFieldType() + "\n");
						preprocess = new TextPreprocess(field.getFieldType());
						processedFieldTypeSb.append(preprocess.doCodeTextProcess() + "\n");
					}
				}
			}

			// 这里是否考虑直接过滤掉为空的，桌面有一个例子，为空的
			// org.apache.tools.ant.taskdefs.optional.junit.CompoundEnumeration 内部类问题
			if (null == clsBlockCommentMap.get(cls.getPackageName() + "." + cls.getClassName())) {
				System.out.println("clsBlockCommentMap 中不存在 " + cls.getPackageName() + "." + cls.getClassName()+",正在退出...");
				System.exit(0);
			}

//			for (Comment comment : clsBlockCommentMap.get(cls.getClassName())) { //wfpan
			for (Comment comment : clsBlockCommentMap.get(cls.getPackageName() + "." + cls.getClassName())) { //wfpan
				if (comment.isBlockComment() || comment.isDocComment()) {
					String comm = comment.toString();
					if (!comm.contains("Licensed under the Apache License") && comm.length() < 1000) {
						commentSb.append(comm + "\n");
//						processComment(comm, cls.getClassName());
						processComment(comm, cls.getPackageName() + "." + cls.getClassName()); // 报名拼接上类名
					}
				}
			}

			if(null==cls || null==cls.getClassName()) continue;
			
			FileIOUtil.writeFile(cls.getClassName(), config.unprocessedClassNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			preprocess = new TextPreprocess(cls.getClassName());
			FileIOUtil.writeFile(preprocess.doCodeTextProcess(), config.processedClassNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(commentSb.toString(), config.unprocessedCommentDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(fieldNameSb.toString(), config.unprocessedFieldNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			FileIOUtil.writeFile(processedFieldNameSb.toString(), config.processedFieldNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(fieldTypeSb.toString(), config.unprocessedFieldTypeDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			FileIOUtil.writeFile(processedFieldTypeSb.toString(), config.processedFieldTypeDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(methodNameSb.toString(), config.unprocessedMethodNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			FileIOUtil.writeFile(processedMethodNameSb.toString(), config.processedMethodNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(paramNameSb.toString(), config.unprocessedParamNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			FileIOUtil.writeFile(processedParamNameSb.toString(), config.processedParamNameDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(paramTypeSb.toString(), config.unprocessedParamTypeDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			FileIOUtil.writeFile(processedParamTypeSb.toString(), config.processedParamTypeDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");

			FileIOUtil.writeFile(invokeMethodNameSb.toString(), config.unprocessedInvokeMethodDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
			FileIOUtil.writeFile(processedInvokeMethodNameSb.toString(), config.processedInvokeMethodDirPath + "/"
					+ (cls.getPackageName() + "." + cls.getClassName()) + ".txt");
		} // for

	}

	private void processComment(String comment, String clsName) {
		comment = comment.replaceAll("(<pre)([\\s\\S]*)(</pre>)", "").replaceAll("(<([^>]*)>)", "")
				.replaceAll("(@author([^\n]*)\n)", "").replaceAll("(@see([^\n]*)\n)", "")
				.replaceAll("(@since([^\n]*)\n)", "").replaceAll("(@throws([^\n]*)\n)", "")
				.replaceAll("(@param([^\n]*)\n)", "").replaceAll("(@version([^\n]*)\n)", "").replaceAll("/\\*", "")
				.replaceAll("/\\*\\*", "").replaceAll("\\*/", "").replaceAll("\\*", "").replaceAll("@[a-zA-z]*", "");

		// TextPreprocess preprocess = new TextPreprocess(comment);
		// FileIOUtil.writeFile(preprocess.doReqTextProcess(),
		// config.processedCommentDirPath + "/" + clsName + ".txt");

		StanfordNLP stanfordNLP = new StanfordNLP();
		String termPairs = stanfordNLP.getTermPair(comment);
		FileIOUtil.continueWriteFile(termPairs, config.processedCommentDirPath + "/" + clsName + ".txt");
	}

}
