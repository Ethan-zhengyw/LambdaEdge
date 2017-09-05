package edgecloud.devicemanager.apiserver;


import java.io.Serializable;
import java.time.LocalDateTime;

public class Device implements Serializable{

    private String id;
    private String deviceDesc;
    private LocalDateTime lastHeartTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public LocalDateTime getLastHeartTime() {
        return lastHeartTime;
    }

    public void setLastHeartTime(LocalDateTime lastHeartTime) {
        this.lastHeartTime = lastHeartTime;
    }
}
