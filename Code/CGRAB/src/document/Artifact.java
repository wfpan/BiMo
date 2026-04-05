package document;

import java.io.Serializable;

/**包含 id 和 text 两个属性*/
public class Artifact implements Serializable {
    public String id;
    public String text;

    public Artifact(String id, String text) {
        this.id = id;
        this.text = text;
    }
}
