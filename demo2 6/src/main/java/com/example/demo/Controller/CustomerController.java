package com.example.demo.Controller;

import com.example.demo.Model.Customer;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Services.CustomerService;
import com.example.demo.Services.impl.FileUploadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerRepository customerRepository;
    private final FileUploadService fileUploadService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, FileUploadService fileUploadService) {
        this.customerRepository = customerRepository;
        this.fileUploadService = fileUploadService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers")
    public String listCustomers(Model model, Authentication authentication) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "/customer/customers-list";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers/add")
    public String addCustomerForm(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "/customer/create-customer";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/customers/add")
    public String addCustomer(@Valid @ModelAttribute("customer") Customer customer,
                              BindingResult bindingResult,
                              @RequestParam("file") MultipartFile file,
                              Model model,
                              RedirectAttributes redirectAttributes) throws IOException {
        logger.debug("addCustomer method called");

        if (bindingResult.hasErrors()) {
            logger.debug("Binding result has errors: " + bindingResult.getAllErrors());
            return "/customer/create-customer";
        }

        // Dosya yükleme işlemi
        if (!file.isEmpty()) {
            try {
                String fileName = fileUploadService.storeFile(file);
                customer.setPhotoUrl(fileName);
                logger.debug("File uploaded successfully: " + fileName);
            } catch (IOException ex) {
                logger.error("Failed to save file: " + ex.getMessage());
                model.addAttribute("errorMessage", "Failed to save file: " + ex.getMessage());
                return "/customer/create-customer";
            }
        } else {
            customer.setPhotoUrl(null);
        }

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers/update/{id}")
    public String updateCustomerForm(@PathVariable("id") Long id, Model model) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
        } else {
            return "redirect:/customers";
        }
        return "/customer/update-customer";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/customers/update/{id}")
    public String updateCustomer(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("customer") Customer customer,
                                 BindingResult bindingResult,
                                 @RequestParam("file") MultipartFile file,
                                 Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/customer/update-customer";
        }

        // Dosya yükleme işlemi
        if (!file.isEmpty()) {
            try {
                String fileName = fileUploadService.storeFile(file);
                customer.setPhotoUrl(fileName);
            } catch (IOException ex) {
                model.addAttribute("errorMessage", "Failed to save file: " + ex.getMessage());
                return "/customer/update-customer";
            }
        }

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            return "redirect:/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers";
        }
    }

    @GetMapping("/customers/search")
    public String searchName(@RequestParam(value = "query") String query, Model model) {
        List<Customer> customers = customerRepository.searchName(query);
        model.addAttribute("customers", customers);
        return "/customer/customers-list";
    }
}
