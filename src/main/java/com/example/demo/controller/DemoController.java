package com.example.demo.controller;

import com.example.demo.service.DemoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.demo.Demo;

import java.util.List;


@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('read')")
    public List<Demo> findAll() {
        return demoService.findAll();
    }

    @PostMapping("/save")
   // @PreAuthorize("hasAnyAuthority('modification')")
    public void save(@RequestBody Demo demo) {
        demoService.save(demo);
    }
}