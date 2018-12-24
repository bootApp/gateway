package com.bootapp.gateway.rpc;
import com.bootapp.service.core.ice.common.RpcServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;

public enum AuthEndpoint {

    INSTANCE;

    public void init() {
        // 加载属性文件
        com.zeroc.Ice.Properties properties = com.zeroc.Ice.Util.createProperties();
        properties.setProperty("Ice.MessageSizeMax", "10240");
        properties.setProperty("AuthServiceProxy", "AuthService:tcp -h localhost -p 10000");
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = properties;
        Communicator communicator = com.zeroc.Ice.Util.initialize(initData);
        ObjectPrx base = communicator.propertyToProxy("AuthServiceProxy");
//        ObjectPrx base = communicator.stringToProxy("UserService:default -p 10000");
        authService = RpcServicePrx.checkedCast(base);
        if (authService == null) {
            throw new Error("Invalid proxy");
        }
    }
    public byte[] query(String path, byte[] params) {
        return authService.query(path, params);
    }
    public byte[] invoke(String path, byte[] params, byte[] body) {
        return authService.invoke(path, params, body);
    }
    public RpcServicePrx authService;

}