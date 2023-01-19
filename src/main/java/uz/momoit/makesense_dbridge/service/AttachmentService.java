package uz.momoit.makesense_dbridge.service;

import uz.momoit.makesense_dbridge.service.dto.CheckTaskDTO;
import uz.momoit.makesense_dbridge.domain.projection.ImageOfTaskResProjection;
import uz.momoit.makesense_dbridge.service.dto.LabelDTO;

import java.util.List;


public interface AttachmentService {
    List<ImageOfTaskResProjection> getImagesOfTask(String userId, Boolean qcCheck, Long attId);

    void save(List<LabelDTO> labelDTOS, Long dtlSeq);

    void checkTask(List<CheckTaskDTO> checkTaskDTOS, Long taskId, String loginId, String qcId);
}
