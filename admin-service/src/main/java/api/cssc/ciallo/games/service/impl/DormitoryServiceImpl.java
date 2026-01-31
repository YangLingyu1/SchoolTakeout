package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Dormitory;
import api.cssc.ciallo.games.repository.DormitoryRepository;
import api.cssc.ciallo.games.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryServiceImpl implements DormitoryService {

    @Autowired
    private DormitoryRepository dormitoryRepository;

    @Override
    public Dormitory getDormitoryById(Integer id) {
        return dormitoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Dormitory> getDormitories() {
        return dormitoryRepository.findAll();
    }

    @Override
    public List<Dormitory> getDormitoriesByNightDelivery(Boolean nightDelivery) {
        return dormitoryRepository.findByNightDelivery(nightDelivery);
    }

    @Override
    public Dormitory createDormitory(Dormitory dormitory) {
        return dormitoryRepository.save(dormitory);
    }

    @Override
    public Dormitory updateDormitory(Dormitory dormitory) {
        return dormitoryRepository.save(dormitory);
    }

    @Override
    public void deleteDormitory(Integer id) {
        dormitoryRepository.deleteById(id);
    }
}