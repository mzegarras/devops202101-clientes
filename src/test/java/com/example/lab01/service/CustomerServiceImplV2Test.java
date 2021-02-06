package com.example.lab01.service;

import com.example.lab01.config.Lab01Properties;
import com.example.lab01.core.domain.Customer;
import com.example.lab01.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CustomerServiceImplV2Test.Configuration.class})
public class CustomerServiceImplV2Test {

    static class Configuration {

        @Bean
        public Lab01Properties lab01Properties(){
            Lab01Properties lab01Properties = new Lab01Properties();
            return lab01Properties;
        }

        @Bean
        public CustomerService customerService(CustomerRepository customerRepository,
                                               CryptoService cryptoService,
                                               Lab01Properties lab01Properties) {
            return new CustomerServiceImplV2(customerRepository,cryptoService,lab01Properties,null);
        }

    }

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CryptoService cryptoService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Lab01Properties lab01Properties;

    @BeforeEach
    void cleanCachesAndMocksAndStubs() {
        lab01Properties.setPanicEnabled(false);
        reset(customerRepository,cryptoService);
    }

    @Test
    public void save_inPanic_ok(){
        // Preparing data
        lab01Properties.setPanicEnabled(true);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setPaterno("P1");
        customer.setNombre("N1");
        customer.setPassword("passwordClear");

        // Mocks & Stubs configuration
        // Business logic execution
        assertThrows(RuntimeException.class,()->{
            customerService.save(customer);
        });

        // Validating mocks behaviour
        // Validating results
    }


    @Test
    public void upper_test(){

        // Preparing data
        // Mocks & Stubs configuration
        // Business logic execution
        String actual = customerService.upper("a");
        // Validating mocks behaviour
        // Validating results
        assertThat(actual).isEqualTo("A");
    }


    @Test
    public void getList_ok(){

        // Preparing data
        Customer c1 = new Customer();
        c1.setCustomerId(1);
        c1.setPaterno("P1");
        c1.setNombre("N1");

        Customer c2 = new Customer();
        c2.setCustomerId(2);
        c2.setPaterno("P2");
        c2.setNombre("N2");

        List<Customer> expected = Arrays.asList(c1,c2);

        // Mocks & Stubs configuration
        when(customerRepository.getList()).thenReturn(expected);
        // Business logic execution
        List<Customer> actual = customerService.getList();

        // Validating mocks behaviour
        verify(customerRepository).getList();
        verifyNoMoreInteractions(customerRepository);

        // Validating results
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getById_ok(){

        // Preparing data
        Customer expected = new Customer();
        expected.setCustomerId(1);
        expected.setPaterno("P1");
        expected.setNombre("N1");


        // Mocks & Stubs configuration
        when(customerRepository.getById(777)).thenReturn(expected);
        // Business logic execution
        Customer actual = customerService.getById(777);

        // Validating mocks behaviour
        verify(customerRepository).getById(777);
        verifyNoMoreInteractions(customerRepository);

        // Validating results
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void save_ok(){
        // Preparing data
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setPaterno("P1");
        customer.setNombre("N1");
        customer.setPassword("passwordClear");

        Customer expected = new Customer();
        expected.setCustomerId(1);
        expected.setPaterno("P1");
        expected.setNombre("N1");
        expected.setPassword("passwordCipher");

        // Mocks & Stubs configuration
        when(customerRepository.save(customer)).thenReturn(1);
        when(cryptoService.encrypt("passwordClear")).thenReturn("passwordCipher");

        // Business logic execution
        customerService.save(customer);

        // Validating mocks behaviour
        verify(customerRepository).save(expected);
        verify(cryptoService).encrypt("passwordClear");
        verifyNoMoreInteractions(customerRepository,cryptoService);

        // Validating results
    }
}
