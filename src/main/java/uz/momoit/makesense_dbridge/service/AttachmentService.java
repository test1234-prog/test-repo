package uz.momoit.makesense_dbridge.service;

import uz.momoit.makesense_dbridge.service.dto.CheckTaskDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskResDTO;
import uz.momoit.makesense_dbridge.service.dto.LabelDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface AttachmentService {
    List<ImageOfTaskResDTO> getImagesOfTask(String userId, Boolean qcCheck, Long attId);

    void save(List<LabelDTO> labelDTOS, Long dtlSeq);

    void checkTask(List<CheckTaskDTO> checkTaskDTOS, Long taskId, String loginId, String qcId);

    void createFileForImportAnnotation(HttpServletResponse response, Long attSeq) throws IOException;

    String getFileName(Long attSeq);

    void createLabels(HttpServletResponse response, Long dtlSeq) throws IOException;
}
