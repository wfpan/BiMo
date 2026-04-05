package experiment.enums;

/**define some final values (full class name) to represent projects, ex:<i>experiment.project.Test</i>*/
public enum ProjectEnum {
	//-------------------------修改1----------------------------------------------//
	
	//--------------The projects used in the requirement traceability work-----------------//
    ITRUST("experiment.project.iTrust"),
    GANTT("experiment.project.Gantt"),
    MAVEN("experiment.project.Maven"),
    INFINISPAN("experiment.project.Infinispan"),
    SEAM("experiment.project.Seam"),
    GROOVY("experiment.project.Groovy"),
    DROOLS("experiment.project.Drools"),
    PIG("experiment.project.Pig"),
    DERBY("experiment.project.Derby"),
    TEST("experiment.project.Test"),
    //--------------The projects used in the key class identification work-----------------//
    ant_main("experiment.project.keyclass.ant_main"),
    argouml("experiment.project.keyclass.argouml"),
    jedit("experiment.project.keyclass.jedit"),
    jhotdraw("experiment.project.keyclass.jhotdraw"),
    jmeter_core("experiment.project.keyclass.jmeter_core"),
    wro4j("experiment.project.keyclass.wro4j"),
    gwtportlets("experiment.project.keyclass.gwtportlets"),
    javaclient("experiment.project.keyclass.javaclient"),
    jgap("experiment.project.keyclass.jgap"),
    Mars("experiment.project.keyclass.Mars"),
    Maze("experiment.project.keyclass.Maze"),
    neuroph("experiment.project.keyclass.neuroph"),
    tomcat("experiment.project.keyclass.tomcat"),
    JPMC("experiment.project.keyclass.JPMC"),
    log4j("experiment.project.keyclass.log4j"),
    PDFBox("experiment.project.keyclass.PDFBox"),
    Xerces("experiment.project.keyclass.Xerces"),
    xuml("experiment.project.keyclass.xuml"),
    TT("experiment.project.Test")
    ;

    String name;

    ProjectEnum(String name) {
        this.name = name;
    }

    /**get the full class name of a project such as <i>experiment.project.Test</i>*/
    public String getName() {
        return name;
    }

  //-------------------------修改2----------------------------------------------//
    /**get the final value according to project name*/
    public static ProjectEnum getProject(String projectName) {
        switch (projectName) {
            case "itrust":
                return ITRUST;
            case "gantt":
                return GANTT;
            case "maven":
                return MAVEN;
            case "infinispan":
                return INFINISPAN;
            case "groovy":
                return GROOVY;
            case "seam":
                return SEAM;
            case "drools":
                return DROOLS;
            case "pig":
                return PIG;
            case "derby":
                return DERBY;
            case "test":
                return TEST;
            case "ant_main":
                return ant_main;
            case "argouml":
                return argouml;
            case "jedit":
                return jedit;
            case "jhotdraw":
                return jhotdraw;
            case "jmeter_core":
                return jmeter_core;
            case "wro4j":
                return wro4j;
            case "gwtportlets":
                return gwtportlets;
            case "javaclient":
                return javaclient;
            case "jgap":
                return jgap;
            case "Mars":
                return Mars;
            case "Maze":
                return Maze;
            case "neuroph":
                return neuroph;
            case "tomcat":
                return tomcat;
            case "JPMC":
                return JPMC;
            case "log4j":
                return log4j;
            case "PDFBox":
                return PDFBox;
            case "Xerces":
                return Xerces;
            case "xuml":
                return xuml;
            default:
                return null;
        }
    }
}
