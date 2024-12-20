package com.example.demo;

import com.example.demo.Model.Cupcake;
import com.example.demo.Repository.CupcakeRepository;
import com.example.demo.Services.CupcakeAlreadyExistsException;
import com.example.demo.Services.CupcakeNotFoundException;
import com.example.demo.Services.CupcakeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CupcakeServiceTest {

    @Mock
    private CupcakeRepository cupcakeRepository;

    @InjectMocks
    private CupcakeService cupcakeService;

    @Test
    void testListAllCupcakes() {
        List<Cupcake> mockCupcakes = List.of(
                new Cupcake("Vanilla Delight", "Sweet Supplies Co.", 150L),
                new Cupcake("Chocolate Dream", "Chocolate Heaven Ltd.", 200L)
        );

        when(cupcakeRepository.findAll()).thenReturn(mockCupcakes);

        List<Cupcake> result = cupcakeService.listAll();

        assertEquals(2, result.size());
        assertEquals("Vanilla Delight", result.get(0).getName());
        assertEquals("Chocolate Dream", result.get(1).getName());
        verify(cupcakeRepository, times(1)).findAll();

        System.out.println("Test Passed: " + getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName());
    }

    @Test
    void testSaveCupcakeSuccess() throws Exception {
        Cupcake cupcake = new Cupcake("Lemon Zest", "Citrus Creations", 170L);


        when(cupcakeRepository.save(cupcake)).thenReturn(cupcake);

        cupcakeService.save(cupcake);


        verify(cupcakeRepository, times(1)).save(cupcake);

        System.out.println("Test Passed: " + getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName());
    }

    @Test
    void testSaveCupcakeAlreadyExists() {
        Cupcake cupcake = new Cupcake("Lemon Zest", "Citrus Creations", 170L);


        when(cupcakeRepository.countById(cupcake.getId())).thenReturn(1L);


        assertThrows(CupcakeAlreadyExistsException.class, () -> cupcakeService.save(cupcake));

        System.out.println("Test Passed: " + getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName());
    }

    @Test
    void testListAllCupcakesWhenEmpty() {

        when(cupcakeRepository.findAll()).thenReturn(List.of());

        List<Cupcake> result = cupcakeService.listAll();

        
        assertTrue(result.isEmpty());
        verify(cupcakeRepository, times(1)).findAll();

        System.out.println("Test Passed: " + getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName());
    }

    @Test
    void testFindByNameSuccess() {
        Cupcake cupcake = new Cupcake("Mint Chocolate", "Minty Fresh Bakes", 210L);

        // Mock repository.findByName() çağrısı
        when(cupcakeRepository.findByName("Mint Chocolate")).thenReturn(cupcake);

        Cupcake result = cupcakeService.findByName2("Mint Chocolate");

        // Doğrulama
        assertNotNull(result);
        assertEquals("Mint Chocolate", result.getName());

        System.out.println("Test Passed: " + getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName());
    }

    @Test
    void testFindByNameNotFound() {
        // Mock repository.findByName() boş döndürsün
        when(cupcakeRepository.findByName("Unknown Name")).thenReturn(null);

        Cupcake result = cupcakeService.findByName2("Unknown Name");

        // Doğrulama
        assertNull(result);

        System.out.println("Test Passed: " + getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName());
    }
}
