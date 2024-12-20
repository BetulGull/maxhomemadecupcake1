package com.example.demo;

import com.example.demo.Controller.OrderController;
import com.example.demo.Model.Cart;
import com.example.demo.Model.Cupcake;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Order;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.CupcakeRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Services.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {Demo2Application.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CupcakeService cupcakeService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CartService cartService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CartRepository cartRepository;
    @MockBean
    private CupcakeRepository cupcakeRepository;
    @MockBean
    private AuthService authService;
    @MockBean
    private OrderRepository orderRepository;

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testShowPorderListPageSuccess() throws Exception {
        Cupcake cupcake = new Cupcake("Mint Chocolate", "Minty Fresh Bakes", 210L);
        when(cupcakeService.findById(1L)).thenReturn(cupcake);

        mockMvc.perform(get("/Product/porderlist/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("cupcake", cupcake))
                .andExpect(view().name("Product/porderlist"));

        verify(cupcakeService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testShowPorderListPageNotFound() throws Exception {
        when(cupcakeService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/Product/porderlist/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(redirectedUrl("/Product"));

        verify(cupcakeService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void testCreateOrderSuccess() throws Exception {
        Cupcake cupcake = new Cupcake("Strawberry Bliss", "Berry Bakes", 150L);
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsername("testUser");

        when(customerRepository.findByUsername("testUser")).thenReturn(customer);
        when(cupcakeService.findById(1L)).thenReturn(cupcake);

        mockMvc.perform(post("/Product/createp/1")
                        .param("quantity", "2")
                        .with(csrf()) // CSRF token ekleniyor
                        .sessionAttr("username", "testUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(customerRepository, times(1)).findByUsername("testUser");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }



    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testViewCartEmpty() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("cart/cartView"));

        verifyNoInteractions(cartService);
    }
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testCheckoutEmptyCart() throws Exception {
        Cart cart = new Cart();

        mockMvc.perform(post("/cart/checkout")
                        .sessionAttr("cart", cart)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(0)).checkout(any(Cart.class)); // `checkout` çağrılmamalı
    }


    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testCheckoutSuccess() throws Exception {

        Cart cart = new Cart();
        cart.addItem(new Cupcake("Vanilla Bliss", "Vanilla Cupcake", 120L), 2);
        cart.setTotalAmount(240.0);


        when(cartService.checkout(any(Cart.class))).thenReturn(new Order());

        mockMvc.perform(post("/cart/checkout")
                        .sessionAttr("cart", cart)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/orders"));


        verify(cartService, times(1)).checkout(any(Cart.class));
    }



    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testDeleteOrderSuccess() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(get("/orders/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/orders"));

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testDeleteOrderNotFound() throws Exception {
        doThrow(new OrderNotFoundException("Order not found")).when(orderService).deleteOrder(1L);

        mockMvc.perform(get("/orders/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage")) // 'errorMessage' doğru kontrol ediliyor
                .andExpect(redirectedUrl("/orders")); // Hedef URL "/orders"

        verify(orderService, times(1)).deleteOrder(1L);
    }



}
