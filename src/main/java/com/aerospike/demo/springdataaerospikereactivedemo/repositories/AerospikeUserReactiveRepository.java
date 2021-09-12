package com.aerospike.demo.springdataaerospikereactivedemo.repositories;

import com.aerospike.demo.springdataaerospikereactivedemo.objects.User;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import reactor.core.publisher.Mono;

public interface AerospikeUserReactiveRepository extends ReactiveAerospikeRepository<User, Object> {
}
