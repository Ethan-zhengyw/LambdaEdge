package edgecloud.lambda;

import edgecloud.deviceserver.server.DeviceServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class DeviceServerRun implements DisposableBean, Runnable{

    private Thread thread;

    DeviceServerRun(){
        this.thread = new Thread(this);
    }

    @Override
    public void run(){
        try {
            new DeviceServer(8082).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(){

    }
}