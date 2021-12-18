package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.training.project.entities.Mistake;
import ua.training.project.repositories.MistakeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MistakeService {

    private final MistakeRepository mistakeRepository;

    @Autowired
    public MistakeService(MistakeRepository mistakeRepository) {
        this.mistakeRepository = mistakeRepository;
    }

    public List<Mistake> getMistakesById(List<Long> mistakesIds){
        return mistakesIds.stream().map(mistakeRepository::getById).collect(Collectors.toList());
    }
}
