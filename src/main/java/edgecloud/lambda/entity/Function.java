package edgecloud.lambda.entity;

import javax.persistence.*;

@Entity
public class Function {
    @Id
    @GeneratedValue
    private Integer id;
    private String funcName;
    private Integer funcVersion;
    private String funcHandler;  // filename.handler-method
    private String funcDesc;
    private String funcRuntime;  // e.g. python2.7
    private Integer funcMemorySize;  // KB
    private Integer funcTimeout;  // Seconds

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

    @Override
    public String toString() {
        return String.format("{\"id\": %d, \"func_name\": %s, \"funcVersion\": %d, \"funcBody\": %s,  \"func_path\": %s," +
                        "\"funcHandler\": %s, \"funcDesc\": %s, \"funcRuntime\": %s, \"funcMemorySize\": %d}",
                id, funcName, funcVersion, funcBody, funcPath,
                funcHandler, funcDesc, funcRuntime, funcMemorySize);
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

    public String getFuncHandler() {
        return funcHandler;
    }

    public void setFuncHandler(String funcHandler) {
        this.funcHandler = funcHandler;
    }

    public String getFuncDesc() {
        return funcDesc;
    }

    public void setFuncDesc(String funcDesc) {
        this.funcDesc = funcDesc;
    }

    public String getFuncRuntime() {
        return funcRuntime;
    }

    public void setFuncRuntime(String funcRuntime) {
        this.funcRuntime = funcRuntime;
    }

    public Integer getFuncMemorySize() {
        return funcMemorySize;
    }

    public void setFuncMemorySize(Integer funcMemorySize) {
        this.funcMemorySize = funcMemorySize;
    }

    public Integer getFuncTimeout() {
        return funcTimeout;
    }

    public void setFuncTimeout(Integer funcTimeout) {
        this.funcTimeout = funcTimeout;
    }
}