package com.pmu2.exec.service;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.exeption.AException;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import com.pmu2.exec.service.mapper.PartantMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PmuPartantService {

    private final PmuCourseService pmuCourseService;
    private final PartantJpaRepository partantJpaRepository;
    private final PartantMapper partantMapper;

    public PmuPartantService(PmuCourseService pmuCourseService, PartantJpaRepository partantJpaRepository, PartantMapper partantMapper) {
        this.pmuCourseService = pmuCourseService;
        this.partantJpaRepository = partantJpaRepository;
        this.partantMapper = partantMapper;
    }

    public List<PartantRecord> findAll() {
        return partantMapper.toReccordList(partantJpaRepository.findAll());
    }

    public PartantRecord save(PartantRecord partant) {
        return partantMapper.toReccord(partantJpaRepository.save(partantMapper.toEntity(partant)));
    }

    public void deleteById(Long id) {
        // In your service or repository class
        if (partantJpaRepository.findById(id).isPresent()){
            pmuCourseService.removePartant(id); // Remove child from parent's collection
            partantJpaRepository.deleteById(id); // Delete the child entity
        }else{
            throw new AException("Partant not found: " + id);
        }
    }

    public List<PartantRecord> findByName(String name) {
        return partantMapper.toReccordList(partantJpaRepository.findByName(name));
    }

    public void existPartants(List<PartantEntity> partants) {
        if (partants.isEmpty()) {
            return;
        }
        partants.forEach(partant -> {
            var result = partantJpaRepository.findByName(partant.getName());
            if (result.isEmpty()) {
                throw new AException("Partant not found: " + partant.getName());
            }
        });
    }

}
