package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.demo.demo.Demo;
import com.example.demo.demo.Details;
import com.example.demo.repository.DetailsRepository;
import com.example.demo.service.DemoService;

//TODO: 1. Этого уже быть не должно

@RestController
@RequestMapping("/details")
@RequiredArgsConstructor
public class DetailsController {

    private final DetailsRepository detailsRepository;
    private final DemoService demoService;

    @PostMapping("/{demo_id}/save")
    public void save(@PathVariable(value = "demo_id") Long demoId,
                     @RequestBody Details details) {
        Demo demo = demoService.findById(demoId);
        details.setDemo(demo);
        detailsRepository.save(details);
    }
}