package uz.momoit.makesense_dbridge.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.momoit.makesense_dbridge.domain.enumeration.VrifysttusEnum;
import uz.momoit.makesense_dbridge.repository.AttachmentRepository;
import uz.momoit.makesense_dbridge.repository.EduResultRepository;
import uz.momoit.makesense_dbridge.repository.LabelRepository;
import uz.momoit.makesense_dbridge.repository.TaskDtlRepository;
import uz.momoit.makesense_dbridge.service.LabelService;
import uz.momoit.makesense_dbridge.domain.projection.LabelOrdersProjection;
import uz.momoit.makesense_dbridge.service.dto.LabelsImportDTO;
import uz.momoit.makesense_dbridge.domain.projection.TaskDtlProjection;
import uz.momoit.makesense_dbridge.service.dto.YoloDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    private final AttachmentRepository attachmentRepository;

    private final EduResultRepository eduResultRepository;

    private final TaskDtlRepository taskDtlRepository;

    private final Logger log = LoggerFactory.getLogger(LabelServiceImpl.class);

    public LabelServiceImpl(LabelRepository labelRepository, AttachmentRepository attachmentRepository, EduResultRepository eduResultRepository, TaskDtlRepository taskDtlRepository) {
        this.labelRepository = labelRepository;
        this.attachmentRepository = attachmentRepository;
        this.eduResultRepository = eduResultRepository;
        this.taskDtlRepository = taskDtlRepository;
    }

    @Override
    public List<LabelsImportDTO> convertLabelToYolo(Long dtlSeq) {
        log.debug("Rest request to convert label to yolo: {} ", dtlSeq);
        TaskDtlProjection taskDtlByDtlSeq = taskDtlRepository.getTaskDtlByDtlSeq(dtlSeq);
        List<Long> attachmentIdsByDtSeq = attachmentRepository.getAttachmentIdsByDtSeq(dtlSeq);
        return attachmentIdsByDtSeq.stream()
                .map(attSeq ->
                    new LabelsImportDTO(
                        attSeq,
                        null,
                        taskDtlByDtlSeq.getLoginId(),
                        taskDtlByDtlSeq.getQcId(),
                        getYoloDTOByAttSeq(attSeq)))
                .map(labelsImportDTO -> {
                    String vrifySttusByAttSeq = eduResultRepository.getVrifySttusByAttSeq(labelsImportDTO.getAttSeq());
                    if(vrifySttusByAttSeq == null) {
                        labelsImportDTO.setVrifysttus(VrifysttusEnum.UNDONE.name());
                        return labelsImportDTO;
                    }
                    switch (vrifySttusByAttSeq) {
                        case "1":
                            labelsImportDTO.setVrifysttus(VrifysttusEnum.UNCHECKED.name());
                            break;
                        case "2":
                            labelsImportDTO.setVrifysttus(VrifysttusEnum.DONE.name());
                            break;
                        case "3":
                            labelsImportDTO.setVrifysttus(VrifysttusEnum.APPROVED.name());
                            break;
                        case "4":
                            labelsImportDTO.setVrifysttus(VrifysttusEnum.REJECTED.name());
                            break;
                        default:
                            labelsImportDTO.setVrifysttus(VrifysttusEnum.UNDONE.name());
                            break;
                    }
                            return labelsImportDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public List<LabelOrdersProjection> getLabelOrders(Long dtlSeq) {
        return labelRepository.getLabelOrdersByDtlSeq(dtlSeq);
    }

    private List<YoloDTO> getYoloDTOByAttSeq(Long attSeq) {
        log.debug("Rest request to get yoloDTO by attSeq: {} ", attSeq);
        return labelRepository.getLabelsByAttSeq(attSeq).stream().map(label -> {
            YoloDTO yoloDTO = new YoloDTO();
            yoloDTO.setLabelOrder(label.getLabelOrder());
            yoloDTO.setYolo1((2* label.getBboxX() + label.getBboxWidth()) / 2.0 * (1 / label.getImgWidth()));
            yoloDTO.setYolo2((2* label.getBboxY() + label.getBboxHeight()) / 2.0 * (1 / label.getImgHeight()));
            yoloDTO.setYolo3(label.getBboxWidth() * (1 / label.getImgWidth()));
            yoloDTO.setYolo4(label.getBboxHeight() * (1 / label.getImgHeight()));
            return yoloDTO;
        }).collect(Collectors.toList());
    }

    public  void updateLabelOrders(Long dtlSeq) {
        List<LabelOrdersProjection> labelDataByDtlSeq = labelRepository.getLabelDataByDtlSeq(dtlSeq);
        Set<String> labelNames = labelDataByDtlSeq.stream().map(LabelOrdersProjection::getLabelName).collect(Collectors.toSet());
        Long cnt = 0L;
        Map<String, Long> labelNameAndOrder = new HashMap<>();
        for (String labelName : labelNames) {
            labelNameAndOrder.put(labelName, cnt);
            cnt++;
        }
        for (LabelOrdersProjection labelOrdersProjection : labelDataByDtlSeq) {
            Long aLong = labelNameAndOrder.get(labelOrdersProjection.getLabelName());
            labelRepository.updateLabelOrder(labelOrdersProjection.getLabelSeq(), aLong);
        }
    }
}
