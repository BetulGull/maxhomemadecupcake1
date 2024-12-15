package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.DTOs.Orderview;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class OrderController {
    @Autowired
    private AuthService authService;

    private OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;


    @Autowired
    public OrderController(OrderService orderService, ProductRepository productRepository, ProductService productService,
                           CustomerService customerService, OrderRepository orderRepository,
                           CustomerRepository customerRepository, CartService cartService, CartRepository cartRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.cartService = cartService;
        this.cartRepository=cartRepository;
    }

    /*public OrderController(OrderService orderService, ProductService productService, CustomerService customerService,ProductRepository productRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
        this.productRepository = productRepository;*/

    @GetMapping("/oindex")
    public String getIndexPage() {
        return "index";
    }


    @GetMapping("/Product/porderlist/{id}")
    public String showPorderListPage(Model model, @PathVariable("id") Long id, RedirectAttributes ra) {
        Order order = new Order();
        Product product = productService.findById(id);


        model.addAttribute("order", order); // Add the order object
        model.addAttribute("product", product); // Add the product object


        return "Product/porderlist"; // Return the order creation form view
    }


    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    @PostMapping("/Product/createp/{id}")
    public String createOrder(Order order, @PathVariable("id") Long productId,
                              @RequestParam("quantity") int quantity,
                              RedirectAttributes ra, HttpSession session) {

        String username = getCurrentUsername();
        if (username == null) {
            return "redirect:/login"; // Redirects to login if username is null
        }

        Customer customer = customerRepository.findByUsername(username);
        Product product = productService.findById(productId);
        if (product == null) {
            ra.addFlashAttribute("errorMessage", "Product not found.");
            return "redirect:/errorPage"; // This could be causing a loop if errorPage redirects back
        }

        // Handle cart in session
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
        }

        cart.addItem(product, quantity); // Add product to cart, automatically updates the total
        session.setAttribute("cart", cart);

        // Save the cart (if necessary)
        cartRepository.save(cart);

        return "redirect:/cart";  // Redirect back to the cart page
    }


    @GetMapping("/order/add")
    public String showAddOrderForm(Model model) {
        model.addAttribute("order", new Orderview());
        return "order/orderAdd";
    }
    @PostMapping("/order/add")
    public String addOrder(Orderview orderv, RedirectAttributes ra) {
        try {
            orderService.createOrder(orderv);
            ra.addFlashAttribute("message", "The order was successfully added");
        } catch (OrderAlreadyExistsException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }








    @GetMapping("/orders")
    public String showOrderList(Model model) {
        // Get the current logged-in username
        String username = getCurrentUsername();

        // Check if the current user is an admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        List<Order> orders;

        if (isAdmin) {
            // If admin, get all orders
            orders = orderRepository.findAll();
        } else {
            // If a regular user, get only their orders
            Customer customer = customerRepository.findByUsername(username);
            if (customer != null) {
                orders = orderRepository.findByCustomer(customer);
            } else {
                orders = new ArrayList<>(); // In case no customer is found, show an empty list
            }
        }

        // Add orders to model
        model.addAttribute("orders", orders);
        return "order/orderList"; // This view will display the orders
    }






    @GetMapping("/order/update/{id}")
    public String getOrderById(@PathVariable Long id, Model model) throws OrderNotFoundException {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order/orderEdit";
    }

    @PostMapping("/order/update/{id}")
    public String updateOrder(Order orderv, RedirectAttributes ra) throws OrderNotFoundException {
        try {
            orderService.updateOrder(orderv);
            ra.addFlashAttribute("message", "The order was successfully updated");
        }catch (CustomerNotFoundException |ProductNotFoundException e)
        {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }


    @GetMapping("/customers/corderlist/{id}")
    public String showCorderListPage(Model model, @PathVariable("id") Long id, RedirectAttributes ra) {
        Order o = new Order();
        Customer customer = customerService.findById(id);
        List<Product> products= (List<Product>) productRepository.findAll();

        model.addAttribute("order", o);
        model.addAttribute("customer", customer);
        model.addAttribute("products",products);
        //model.addAttribute("product_name", o.getProduct().getName());
        //model.addAttribute("customerName", customerService.findCustomerNameById(id));
        // model.addAttribute("product_name", productService.findProductNameById(id));
        return "customer/corderlist";
    }




    @PostMapping("/customers/createc/{id}")
    public String createOrderc( Order order, RedirectAttributes ra) throws CustomerNotFoundException {

        order.setId(order.getId());
        order.setDate(order.getDate());
        order.setDeliveryStatus(order.getDeliveryStatus());
        order.setCity(order.getCity());
        order.setCustomer(order.getCustomer());
        order.setProduct(order.getProduct());
        try {
            orderService.saveOrder(order);
            ra.addFlashAttribute("message", "The order was successfully added");
        } catch (OrderAlreadyExistsException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/orders";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            model.addAttribute("message", "Your cart is empty.");
        } else {
            model.addAttribute("cart", cart);
        }
        return "cart/cartView"; // A view to display the cart contents
    }





    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty.");
            return "redirect:/cart";  // Redirect back to the cart page if it's empty
        }

        // Create the order from the cart
        try {
            Order order = cartService.checkout(cart);
            session.removeAttribute("cart"); // Clear cart after checkout

            redirectAttributes.addFlashAttribute("message", "Order created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error during checkout: " + e.getMessage());
        }

        return "redirect:/orders";  // Redirect to order list after checkout
    }









    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id , RedirectAttributes ra) {
        try {
            orderService.deleteOrder(id);
            ra.addFlashAttribute("message", "Order deleted successfully");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("error", "Failed to delete order: " + e.getMessage());

        }
        return "redirect:/orders";
    }
}

