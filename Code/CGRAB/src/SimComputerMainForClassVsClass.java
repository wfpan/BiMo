import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.IR_Only;
import document.TextDataset;
import experiment.Result;
import experiment.enums.IREnum;
import experiment.enums.ProjectEnum;
import experiment.project.Project;
import ir.IR;
import ir.IR_TAROT;
import ir.YuJianLing;
import ir.YuJianLingClassVsClass;
import ir.model.IRModel;

/** 求 Class与class之间的相似度
 * 	修改3处地方：
 *  1. 本页中的 修改1和修改2
 *  2. YJL_LSI 中的修改3
 *  
 *  */
public class SimComputerMainForClassVsClass {
	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		long startTime = System.currentTimeMillis();
		String projectClassName = ProjectEnum.ant_main.getName(); // 修改1：select a project, ex: Test
		projectClassName = ProjectEnum.argouml.getName();
		projectClassName = ProjectEnum.gwtportlets.getName();
		projectClassName = ProjectEnum.javaclient.getName();
		projectClassName = ProjectEnum.jedit.getName();
		projectClassName = ProjectEnum.jgap.getName();
		projectClassName = ProjectEnum.jhotdraw.getName();
		projectClassName = ProjectEnum.jmeter_core.getName();
		projectClassName = ProjectEnum.JPMC.getName();
		projectClassName = ProjectEnum.log4j.getName();
		projectClassName = ProjectEnum.Mars.getName();
		projectClassName = ProjectEnum.neuroph.getName();
		projectClassName = ProjectEnum.PDFBox.getName();
		projectClassName = ProjectEnum.tomcat.getName();
		projectClassName = ProjectEnum.wro4j.getName();
		projectClassName = ProjectEnum.Xerces.getName();
		projectClassName = ProjectEnum.xuml.getName();
		projectClassName = ProjectEnum.Maze.getName();
		
		
//      Class projectClass = Class.forName(ProjectEnum.ant_main.getName()); // select a project, ex: Test
//		Class projectClass = Class.forName(ProjectEnum.argouml.getName());
//      Class projectClass = Class.forName(ProjectEnum.gwtportlets.getName());
		Class projectClass = Class.forName(projectClassName);

		Class irModelClass = Class.forName("com.wfpan.yjl.model.YJL_LSI_ClassVsClass"); // 修改2：select an IR model, ex: LSI
		//LSI求得结果为0 是因为所有biterm在所有文档中都出现了，所以 IDF 为0，为0是正常的

		
//		Class irModelClass = Class.forName("com.wfpan.yjl.model.YJL_JSD"); 
//		Class irModelClass = Class.forName("com.wfpan.yjl.model.YJL_VSM"); 
		// com.wfpan.yjl.YJL

		Project project = (Project) projectClass.newInstance();
		System.out.println(project.getProjectName());
		System.setProperty("project", project.getProjectName());
		IRModel irModel = (IRModel) irModelClass.newInstance();
		System.out.println(irModel);

		TextDataset textDataset = new TextDataset(project.getReqPath(), project.getClassDirPath(),
				project.getRtmClassPath(),true);

		Map<String, Result> resultMap = new HashMap<>();

		YuJianLingClassVsClass yjl = new YuJianLingClassVsClass();
		Result result_ir = yjl.compute(textDataset, irModel, project);
		long endTime = System.currentTimeMillis();
//        System.out.println("time cost:" + (endTime - startTime) * 1.0 / 1000 / 60);
		System.out.println("time cost:" + (endTime - startTime) * 1.0 / 1000);
	}

}
