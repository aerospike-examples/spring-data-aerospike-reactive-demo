package com.aerospike.demo.springdataaerospikereactivedemo;

import com.aerospike.demo.springdataaerospikereactivedemo.objects.User;
import com.aerospike.demo.springdataaerospikereactivedemo.repositories.AerospikeUserReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
class AerospikeUserReactiveRepositoryTests {

    @Autowired
    AerospikeUserReactiveRepository aerospikeUserReactiveRepository;

    private User user1, user2, user3, user4;

    @BeforeEach
    public void cleanUp() {
        aerospikeUserReactiveRepository.deleteAll();
        user1 = new User(1, "userName1", "userName1@gmail.com", 31);
        user2 = new User(2, "userName2", "userName2@gmail.com", 32);
        user3 = new User(3, "userName3", "userName3@gmail.com", 33);
        user4 = new User(4, "userName4", "userName4@gmail.com", 34);

        StepVerifier.create(aerospikeUserReactiveRepository.saveAll(Flux.just(user1, user2, user3, user4))).expectNextCount(4).verifyComplete();
    }

    @Test
    public void findById() {
        StepVerifier.create(aerospikeUserReactiveRepository.findById(user1.getId())
                .subscribeOn(Schedulers.parallel())).consumeNextWith(actual ->
                assertThat(actual).isEqualTo(user1)
        ).verifyComplete();
    }

    @Test
    public void findById_ShouldNotReturnNotExistent() {
        StepVerifier.create(aerospikeUserReactiveRepository.findById("non-existent-id")
                .subscribeOn(Schedulers.parallel()))
                .expectNextCount(0).verifyComplete();
    }

    @Test
    public void existsById_ShouldReturnTrueWhenExists() {
        StepVerifier.create(aerospikeUserReactiveRepository.existsById(user2.getId()).subscribeOn(Schedulers.parallel()))
                .expectNext(true).verifyComplete();
    }

    @Test
    public void existsByIdPublisher_ShouldCheckOnlyFirstElement() {
        StepVerifier.create(aerospikeUserReactiveRepository.existsById(Flux.just(user1.getId(), "non-existent-id"))
                .subscribeOn(Schedulers.parallel()))
                .expectNext(true).verifyComplete();
    }

    @Test
    public void delete_ShouldDeleteExistent() {
        StepVerifier.create(aerospikeUserReactiveRepository.delete(user3).subscribeOn(Schedulers.parallel())).verifyComplete();

        StepVerifier.create(aerospikeUserReactiveRepository.findById(user3.getId())).expectNextCount(0).verifyComplete();
    }

    @Test
    public void deleteAllPublisher_ShouldSkipNonexistent() {
        User nonExistentUser = new User(9, "nonExistingUser", "nonEistingUser@gmail.com", 75);

        aerospikeUserReactiveRepository.deleteAll(Flux.just(user1, nonExistentUser, user4)).subscribeOn(Schedulers.parallel()).block();

        StepVerifier.create(aerospikeUserReactiveRepository.findById(user1.getId())).expectNextCount(0).verifyComplete();
        StepVerifier.create(aerospikeUserReactiveRepository.findById(user4.getId())).expectNextCount(0).verifyComplete();
    }

    @Test
    public void saveEntityShouldInsertNewEntity() {
        User newUserToSave = new User(5, "newUserToSave", "newUserToSave@gmail.com", 40);

        StepVerifier.create(aerospikeUserReactiveRepository.save(newUserToSave).subscribeOn(Schedulers.parallel())).expectNext(newUserToSave).verifyComplete();

        assertUserExistsInRepo(newUserToSave);
    }

    @Test
    public void savePublisherOfMixedEntitiesShouldInsertNewAndUpdateOld() {
        User newUserToSave1 = new User(5, "newUserToSave1", "newUserToSave1@gmail.com", 41);
        User newUserToSave2 = new User(6, "newUserToSave2", "newUserToSave2@gmail.com", 42);
        User newUserToSave3 = new User(7, "newUserToSave3", "newUserToSave3@gmail.com", 43);

        StepVerifier.create(aerospikeUserReactiveRepository.save(newUserToSave1).subscribeOn(Schedulers.parallel()))
                .expectNext(newUserToSave1).verifyComplete();

        user1.setEmail("newUserToSave1NewEmail");
        user1.setAge(51);

        StepVerifier.create(aerospikeUserReactiveRepository.saveAll(Flux.just(newUserToSave1, newUserToSave2, newUserToSave3))).expectNextCount(3).verifyComplete();

        assertUserExistsInRepo(newUserToSave1);
        assertUserExistsInRepo(newUserToSave2);
        assertUserExistsInRepo(newUserToSave3);
    }

    private void assertUserExistsInRepo(User user) {
        StepVerifier.create(aerospikeUserReactiveRepository.findById(user.getId())).consumeNextWith(actual -> {
            assertThat(actual.getName()).isEqualTo(user.getName());
            assertThat(actual.getEmail()).isEqualTo(user.getEmail());
            assertThat(actual.getAge()).isEqualTo(user.getAge());
        }).verifyComplete();
    }
}
