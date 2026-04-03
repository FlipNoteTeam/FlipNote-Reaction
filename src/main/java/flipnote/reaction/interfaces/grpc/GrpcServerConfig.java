package flipnote.reaction.interfaces.grpc;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GrpcServerConfig implements SmartLifecycle {

	private final Server server;
	private boolean running;

	public GrpcServerConfig(
		@Value("${grpc.server.port}") int port,
		ReactionGrpcService reactionGrpcService
	) {
		this.server = ServerBuilder.forPort(port)
			.addService(reactionGrpcService)
			.build();
	}

	@Override
	public void start() {
		try {
			server.start();
			running = true;
			log.info("gRPC server started on port {}", server.getPort());
		} catch (IOException e) {
			throw new RuntimeException("Failed to start gRPC server", e);
		}
	}

	@Override
	public void stop() {
		if (server != null) {
			server.shutdown();
			running = false;
			log.info("gRPC server stopped");
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}
}
