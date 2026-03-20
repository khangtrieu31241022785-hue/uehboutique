package com.uehboutique.controller;

import com.uehboutique.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;


    @GetMapping
    public ResponseEntity<?> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }
    @PostMapping
    public ResponseEntity<?> addService(@RequestBody com.uehboutique.entity.Service service) {
        return ResponseEntity.ok(serviceService.addService(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Integer id, @RequestBody com.uehboutique.entity.Service service) {
        try {
            return ResponseEntity.ok(serviceService.updateService(id, service));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Integer id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok("Service deleted successfully");
    }
}
