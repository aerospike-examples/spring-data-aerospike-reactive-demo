package com.aerospike.demo.springdataaerospikereactivedemo;

import com.aerospike.demo.springdataaerospikereactivedemo.configuration.AerospikeReactiveConfiguration;
import com.aerospike.demo.springdataaerospikereactivedemo.objects.User;
import com.aerospike.demo.springdataaerospikereactivedemo.repositories.AerospikeUserReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
class AerospikeUserReactiveRepositoryTests {

	@Autowired
	AerospikeUserReactiveRepository aerospikeUserReactiveRepository;

	@Test
	public void saveUser() {
		User user1 = new User(1, "userName1", "userName1@gmail.com", 30);

		StepVerifier.create(aerospikeUserReactiveRepository.save(user1).subscribeOn(Schedulers.parallel())).expectNext(user1).verifyComplete();

		StepVerifier.create(aerospikeUserReactiveRepository.findById(user1.getId())
				.subscribeOn(Schedulers.parallel())).consumeNextWith(actual ->
				assertThat(actual).isEqualTo(user1)
		).verifyComplete();
	}
}
