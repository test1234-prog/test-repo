package uz.momoit.makesense_dbridge.service;

import uz.momoit.makesense_dbridge.service.dto.CheckTaskDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskResDTO;
import uz.momoit.makesense_dbridge.service.dto.LabelDTO;

import java.util.List;


public interface AttachmentService {
    List<ImageOfTaskResDTO> getImagesOfTask(String userId, Long attId);

    void save(List<LabelDTO> labelDTOS, Long dtlSeq);

    void checkTask(List<CheckTaskDTO> checkTaskDTOS, Long taskId);
}
