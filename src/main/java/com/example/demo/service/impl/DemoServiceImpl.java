package com.example.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.demo.Demo;
import com.example.demo.repository.DemoRepository;
import com.example.demo.repository.DetailsRepository;
import com.example.demo.service.DemoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoServiceImpl implements DemoService {

    private final DemoRepository demoRepository;
    private final DetailsRepository detailsRepository;

    @Override
    public void save(Demo demo) {
        demo.getDetails().forEach(details -> {
            details.setDemo(demo);
            detailsRepository.save(details);
        });
        demoRepository.save(demo);
    }

    @Override
    public List<Demo> findAll() {
        return demoRepository.findAll();
    }

    @Override
    public Demo findById(long id) {
        return demoRepository.findById(id).orElse(new Demo());
    }
}