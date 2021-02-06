package com.example.lab01.controller.web;


import com.example.lab01.controller.web.dto.CustomerWebDto;
import com.example.lab01.core.domain.Customer;
import com.example.lab01.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
@ContextConfiguration(classes = {CustomerControllerTest.Configuration.class})
public class CustomerControllerTest {

    public static class Configuration {

        @Bean
        public CustomerController customerController(CustomerService customerService){
            return new CustomerController(customerService);
        }

    }

    @MockBean
    private CustomerService customerService;

   @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanCachesAndMocksAndStubs() {
        reset(customerService);
    }

    @Test
    void doGet_error() throws Exception {

        // Preparing data
        // Mocks & Stubs configuration
        when(customerService.getList()).thenThrow(new RuntimeException());

        // Business logic execution
        mockMvc.perform(get("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        // Validating mocks behaviour
        verify(customerService).getList();
        verifyNoMoreInteractions(customerService);

        // Validating results

    }
    @Test
    void doGet_ok() throws Exception {
        // Preparing data
        Customer customer1 = new Customer();
        customer1.setCustomerId(1);
        customer1.setNombre("nombre1");
        customer1.setPaterno("paterno1");

        Customer customer2 = new Customer();
        customer2.setCustomerId(2);
        customer2.setNombre("nombre2");
        customer2.setPaterno("paterno2");

        List<Customer> list = Arrays.asList(customer1,customer2);

        // Mocks & Stubs configuration
        when(customerService.getList()).thenReturn(list);

        // Business logic execution
        mockMvc.perform(get("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("customers[0].id").value("1"))
                .andExpect(jsonPath("customers[0].nombre").value("nombre1"))
                .andExpect(jsonPath("customers[0].paterno").value("paterno1"))
                .andExpect(jsonPath("customers[1].id").value("2"))
                .andExpect(jsonPath("customers[1].nombre").value("nombre2"))
                .andExpect(jsonPath("customers[1].paterno").value("paterno2"));

        // Validating mocks behaviour
        verify(customerService).getList();
        verifyNoMoreInteractions(customerService);

        // Validating results

    }

    @Test
    void doGetById_ok() throws Exception {
        // Preparing data
        Customer customer1 = new Customer();
        customer1.setCustomerId(1);
        customer1.setNombre("nombre1");
        customer1.setPaterno("paterno1");

        // Mocks & Stubs configuration
        when(customerService.getById(1)).thenReturn(customer1);

        // Business logic execution
        mockMvc.perform(get("/customers/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("customer.id").value("1"))
                .andExpect(jsonPath("customer.nombre").value("nombre1"))
                .andExpect(jsonPath("customer.paterno").value("paterno1"));

        // Validating mocks behaviour
        verify(customerService).getById(1);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }

    @Test
    void doGetById_error404() throws Exception {
        // Preparing data
        // Mocks & Stubs configuration
        when(customerService.getById(666)).thenReturn(null);

        // Business logic execution
        mockMvc.perform(get("/customers/666")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Validating mocks behaviour
        verify(customerService).getById(666);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }

    @Test
    void doPostSync_ok() throws Exception {
        // Preparing data
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setNombre("nombre1");
        customer.setPaterno("paterno1");
        CustomerWebDto customerWebDto = new CustomerWebDto();
        customerWebDto.setCustomer(customer);

        String json = new ObjectMapper().writeValueAsString(customerWebDto);

        // Mocks & Stubs configuration
        doNothing().when(customerService).save(customer);


        // Business logic execution
        mockMvc.perform(post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .header(CustomerController.X_API_FORCE_SYC_HEADER, "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        // Validating mocks behaviour
        verify(customerService).save(customer);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }


    @Test
    void doPostAsync_ok() throws Exception {
        // Preparing data
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setNombre("nombre1");
        customer.setPaterno("paterno1");
        CustomerWebDto customerWebDto = new CustomerWebDto();
        customerWebDto.setCustomer(customer);

        String json = new ObjectMapper().writeValueAsString(customerWebDto);

        // Mocks & Stubs configuration
        doNothing().when(customerService).save(customer);


        // Business logic execution
        mockMvc.perform(post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(CustomerController.X_API_FORCE_SYC_HEADER, "false")
                .content(json))
                .andExpect(status().isAccepted());

        // Validating mocks behaviour
        verify(customerService).save(customer);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }


    @Test
    void doPutSync_ok() throws Exception {
        // Preparing data
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setNombre("nombre1");
        customer.setPaterno("paterno1");
        CustomerWebDto customerWebDto = new CustomerWebDto();
        customerWebDto.setCustomer(customer);

        String json = new ObjectMapper().writeValueAsString(customerWebDto);

        // Mocks & Stubs configuration
        doNothing().when(customerService).update(customer);


        // Business logic execution
        mockMvc.perform(put("/customers/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(CustomerController.X_API_FORCE_SYC_HEADER, "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        // Validating mocks behaviour
        verify(customerService).update(customer);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }


    @Test
    void doPutAsync_ok() throws Exception {
        // Preparing data
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setNombre("nombre1");
        customer.setPaterno("paterno1");
        CustomerWebDto customerWebDto = new CustomerWebDto();
        customerWebDto.setCustomer(customer);

        String json = new ObjectMapper().writeValueAsString(customerWebDto);

        // Mocks & Stubs configuration
        doNothing().when(customerService).update(customer);


        // Business logic execution
        mockMvc.perform(put("/customers/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(CustomerController.X_API_FORCE_SYC_HEADER, "false")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isAccepted());

        // Validating mocks behaviour
        verify(customerService).update(customer);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }


    @Test
    void doDeleteAsync_ok() throws Exception {
        // Preparing data
        Customer customer = new Customer();
        customer.setCustomerId(1);


        // Mocks & Stubs configuration
        doNothing().when(customerService).delete(customer);


        // Business logic execution
        mockMvc.perform(delete("/customers/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(CustomerController.X_API_FORCE_SYC_HEADER, "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        // Validating mocks behaviour
        verify(customerService).delete(customer);
        verifyNoMoreInteractions(customerService);

        // Validating results
    }

    @Test
    void doDeleteAsync_Error_NotFound() throws Exception {
        // Preparing data
        // Mocks & Stubs configuration

        // Business logic execution
        mockMvc.perform(delete("/customers/666")
                .accept(MediaType.APPLICATION_JSON)
                .header(CustomerController.X_API_FORCE_SYC_HEADER, "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Validating mocks behaviour
        verifyNoMoreInteractions(customerService);

        // Validating results
    }

}
