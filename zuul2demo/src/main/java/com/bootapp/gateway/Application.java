package com.bootapp.gateway;

import com.bootapp.gateway.rpc.AuthEndpoint;
import com.google.inject.Injector;
import com.netflix.config.ConfigurationManager;
import com.netflix.governator.InjectorBuilder;
import com.netflix.zuul.netty.server.BaseServerStartup;
import com.netflix.zuul.netty.server.Server;

public class Application {
    public static void main(String[] args) {
        new Application().start(args);
    }

    private void start(String[] args) {
        System.out.println("Zuul Sample: starting up.");
        long startTime = System.currentTimeMillis();
        int exitCode = 0;
        Server server = null;

        try {
            // ICE communicator
            AuthEndpoint.INSTANCE.init();
            // Zuul
            ConfigurationManager.loadCascadedPropertiesFromResources("application");
            Injector injector = InjectorBuilder.fromModule(new ZuulModule()).createInjector();
            BaseServerStartup serverStartup = injector.getInstance(BaseServerStartup.class);
            server = serverStartup.server();

            long startupDuration = System.currentTimeMillis() - startTime;
            System.out.println("Zuul Sample: finished startup. Duration = " + startupDuration + " ms");

            server.start(true);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.err.println("###############");
            System.err.println("Zuul Sample: initialization failed. Forcing shutdown now.");
            System.err.println("###############");
            exitCode = 1;
        }
        finally {
            // server shutdown
            if (server != null) server.stop();

            System.exit(exitCode);
        }
    }
}
