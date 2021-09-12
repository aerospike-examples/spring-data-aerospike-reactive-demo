package com.aerospike.demo.springdataaerospikereactivedemo.configuration;

import com.aerospike.client.Host;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NioEventLoops;
import com.aerospike.demo.springdataaerospikereactivedemo.repositories.AerospikeUserReactiveRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = AerospikeUserReactiveRepository.class)
public class AerospikeReactiveConfiguration extends AbstractReactiveAerospikeDataConfiguration {
    @Override
    protected Collection<Host> getHosts() {
        return Collections.singleton(new Host("localhost", 3000));
    }

    @Override
    protected String nameSpace() {
        return "test";
    }

    @Override
    protected EventLoops eventLoops() {
        return new NioEventLoops();
    }
}
