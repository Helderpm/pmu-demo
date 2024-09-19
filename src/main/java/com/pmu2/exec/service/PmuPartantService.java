package com.pmu2.exec.service;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import com.pmu2.exec.service.mapper.PartantMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PmuPartantService {

    private final PartantJpaRepository partantJpaRepository;
    private final PartantMapper partantMapper;

    public PmuPartantService(PartantJpaRepository partantJpaRepository, PartantMapper partantMapper) {
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
        partantJpaRepository.deleteById(id);
    }

    public List<PartantRecord> findByName(String name) {
        return partantMapper.toReccordList(partantJpaRepository.findByName(name));
    }

}
