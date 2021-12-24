package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.training.project.dto.input.MistakeSearchDTO;
import ua.training.project.entities.Mistake;
import ua.training.project.repositories.MistakeRepository;
import ua.training.project.utils.ConstantHolder;

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

    public Mistake getMistakeById(Long mistakeId){
        return mistakeRepository.getById(mistakeId);
    }

    public Page<Mistake> getMistakesByName(MistakeSearchDTO mistakeSearchDTO){
        return mistakeRepository.findAllByNameIgnoreCaseContaining(mistakeSearchDTO.getMistakeName(),
                PageRequest.of(mistakeSearchDTO.getPageNumber(), ConstantHolder.MAX_RECORDS_PER_PAGE,
                        Sort.by(Sort.Direction.valueOf(mistakeSearchDTO.getSortDirection()), mistakeSearchDTO.getSort())));
    }

    public void saveMistake(Mistake mistake){
        mistakeRepository.save(mistake);
    }

}
