package experiment.enums;

/** define some final values (full class names of some IR models), ex:<i>ir.model.LSI</i> */
public enum IREnum {
	VSM("ir.model.VSM"), LSI("ir.model.LSI"), JSD("ir.model.JSD");

	private final String model;

	private IREnum(String model) {
		this.model = model;
	}

	/**get the full name of a particular IR model, ex:<i>ir.model.LSI</i>*/
	public String getModel() {
		return model;
	}
}
