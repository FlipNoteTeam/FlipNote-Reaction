package flipnote.reaction.infrastructure.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcConfig {

	@Bean
	public ManagedChannel cardSetChannel(
		@Value("${grpc.cardset.host}") String host,
		@Value("${grpc.cardset.port}") int port
	) {
		return ManagedChannelBuilder.forAddress(host, port)
			.usePlaintext()
			.build();
	}
}
