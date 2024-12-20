package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.example.demo.Model.Cupcake;
import com.example.demo.Repository.CupcakeRepository;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = Demo2Application.class)
@ComponentScan(basePackages = "com.example.demo")
class ProductListTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CupcakeRepository cupcakeRepository;

    @BeforeEach
    void setUp() {
        // Mock data for the test
        Cupcake cupcake1 = new Cupcake("Chocolate Dream", "Chocolate Heaven Ltd.", 200L);
        Cupcake cupcake2 = new Cupcake("Vanilla Delight", "Sweet Supplies Co.", 150L);
        cupcakeRepository.save(cupcake1);
        cupcakeRepository.save(cupcake2);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")

    void testProductListTemplateRendering() throws Exception {
        mockMvc.perform(get("/Product"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>MENU</h1>")))
                .andExpect(content().string(containsString("Chocolate Dream")))
                .andExpect(content().string(containsString("Vanilla Delight")));
    }

}
