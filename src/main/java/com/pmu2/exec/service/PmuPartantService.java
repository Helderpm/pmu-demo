package com.pmu2.exec.service;

import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PmuPartantService {

    @Autowired
    private PartantJpaRepository partantJpaRepository;

    public List<PartantEntity> findAll() {
        return partantJpaRepository.findAll();
    }

    public PartantEntity save(PartantEntity partant) {
        return partantJpaRepository.save(partant);
    }

    public void deleteById(Long id) {
        partantJpaRepository.deleteById(id);
    }

    public List<PartantEntity> findByName(String name) {
        return partantJpaRepository.findByName(name);
    }


}
