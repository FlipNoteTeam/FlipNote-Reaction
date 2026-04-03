package flipnote.reaction.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import flipnote.reaction.infrastructure.grpc.CardSetGrpcClient;
import io.grpc.ManagedChannel;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestGrpcConfig {

	@Bean
	public ManagedChannel cardSetChannel() {
		return mock(ManagedChannel.class);
	}

	@Bean
	public CardSetGrpcClient cardSetGrpcClient() {
		return mock(CardSetGrpcClient.class);
	}
}
