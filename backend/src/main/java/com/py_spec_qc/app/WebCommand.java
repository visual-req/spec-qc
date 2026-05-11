package com.py_spec_qc.app;

import com.py_spec_qc.core.config.AppConfig;
import com.py_spec_qc.core.config.ConfigLoader;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "web", description = "Start a local web UI")
public final class WebCommand implements Runnable {
    @Option(names = "--host", required = false)
    private String host;

    @Option(names = "--port", required = false)
    private Integer port;

    @Override
    public void run() {
        AppConfig cfg = new ConfigLoader().load();
        String resolvedHost = (host == null || host.isBlank()) ? (cfg.serverHost == null || cfg.serverHost.isBlank() ? "127.0.0.1" : cfg.serverHost) : host;
        int resolvedPort = (port == null || port <= 0) ? (cfg.serverPort == null || cfg.serverPort <= 0 ? 8765 : cfg.serverPort) : port;
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(WebApplication.class)
                .properties(
                        "server.address=" + resolvedHost,
                        "server.port=" + resolvedPort,
                        "spring.servlet.multipart.max-file-size=200MB",
                        "spring.servlet.multipart.max-request-size=200MB"
                )
                .run();
        String url = "http://" + resolvedHost + ":" + resolvedPort + "/";
        System.out.println(url);
        try {
            ctx.getBean(BlockingLifecycle.class).block();
        } finally {
            ctx.close();
        }
    }
}
