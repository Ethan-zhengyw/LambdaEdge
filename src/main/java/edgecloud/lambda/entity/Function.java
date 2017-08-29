package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Function {
    @Id
    @GeneratedValue
    private Integer id;

    private String funcName;

    private String funcPath;

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getFuncName() {
        return this.funcName;
    }

    public void setFuncPath(String funcPath) {
        this.funcPath = funcPath;
    }

    public String getFuncPath() {
        return this.funcPath;
    }

    public String toString() {
        return String.format("{\"id\": %d, \"func_name\": %s, \"func_path\": %s}", id, funcName, funcPath);
    }
}
