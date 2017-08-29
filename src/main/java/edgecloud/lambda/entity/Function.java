package edgecloud.lambda.entity;

import edgecloud.lambda.utils.FunctionIO;
import edgecloud.lambda.utils.GetConfig;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.IOException;

@Entity
public class Function {
    @Id
    @GeneratedValue
    private Integer id;

    private String funcName;

    private Integer funcVersion;

    @Column(columnDefinition="TEXT")
    private String funcBody;

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
        return String.format("{\"id\": %d, \"func_name\": %s, \"funcVersion\": %d, \"funcBody\": %s,  \"func_path\": %s}",
                id, funcName, funcVersion, funcBody, funcPath);
    }

    public Integer getFuncVersion() {
        return funcVersion;
    }

    public void setFuncVersion(Integer funcVersion) {
        this.funcVersion = funcVersion;
    }

    public void setFuncBody(String funcBody) {
        this.funcBody = funcBody;
    }

    public String getFuncBody() {
        return funcBody;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}