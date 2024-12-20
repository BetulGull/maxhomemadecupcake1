package com.example.demo.Controller;

import com.example.demo.Model.Cupcake;
import com.example.demo.Services.CupcakeAlreadyExistsException;
import com.example.demo.Services.CupcakeNotFoundException;
import com.example.demo.Services.CupcakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CupcakeController {
    @Autowired
    private CupcakeService cupcakeService;
    @GetMapping("/pindex")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/Product")
    public String showProductList(Model model) {
        List<Cupcake> cupcakeList = cupcakeService.listAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("cupcakeList", cupcakeList); // Corrected attribute name
        model.addAttribute("isAdmin", isAdmin);  // Add isAdmin to the model

        return "Product/ProductList";
    }


    @PostMapping("/Product/save")
    public String saveProduct(@ModelAttribute Cupcake cupcake, RedirectAttributes ra) throws CupcakeAlreadyExistsException {
       try {
           cupcakeService.save(cupcake);
           ra.addFlashAttribute("message", "The product has been saved successfully :)");

       }catch (CupcakeAlreadyExistsException e)
       {
          ra.addFlashAttribute("message",e.getMessage());
       }
        return "redirect:/Product";
    }

    @GetMapping("/Product/create")
    public String showNewForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                model.addAttribute("product", new Cupcake());
                return "Product/ProductAdd";
            } else {
                return "redirect:/Product";  // Redirect non-admin users back to the product list
            }
        }
        return "redirect:/login";  // Redirect to login if the user is not authenticated
    }


    @GetMapping("/Product/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Cupcake cupcake = cupcakeService.get(id);
            model.addAttribute("product", cupcake);
        } catch (CupcakeNotFoundException e) {
            ra.addFlashAttribute("message", "Product not found :(");
            return "redirect:/Product";
        }
        return "Product/ProductEdit";
    }

    @PostMapping("/Product/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute Cupcake cupcake, RedirectAttributes ra) {
        try {
            cupcakeService.updateProduct(id, cupcake);
            ra.addFlashAttribute("message", "The product has been updated successfully :)");
        } catch (CupcakeNotFoundException e) {
            ra.addFlashAttribute("message", "Product not found :(");
        }
        return "redirect:/Product";
    }


    @GetMapping("/Product/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            cupcakeService.delete(id);
            ra.addFlashAttribute("message", "The product with ID " + id + " has been deleted");
        } catch (CupcakeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/Product";
    }
}
