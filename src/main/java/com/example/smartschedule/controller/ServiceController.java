package com.example.smartschedule.controller;

import com.example.smartschedule.entity.Service;
import com.example.smartschedule.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Service createService(@RequestBody Service service) {
        return serviceService.createService(service);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Service> getAllServices() {
        return serviceService.getAllServices();
    }
}
