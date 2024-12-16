package com.tcs.vetclinic;

import com.tcs.vetclinic.client.TestPersonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestApplicationConfig.class)
@ActiveProfiles("test")
@EnableFeignClients
public abstract class IntegrationTest {

    @Autowired
    public TestPersonClient testPersonClient;
}
