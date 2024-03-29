package uz.momoit.makesense_dbridge.service;

import uz.momoit.makesense_dbridge.domain.projection.LabelOrdersProjection;
import uz.momoit.makesense_dbridge.service.dto.LabelsImportDTO;
import uz.momoit.makesense_dbridge.service.dto.YoloDTO;

import java.util.List;

public interface LabelService {
    List<LabelsImportDTO> convertLabelToYolo(Long dtlSeq);

    List<LabelOrdersProjection> getLabelOrders(Long dtlSeq);

    void updateLabelOrders(Long dtlSeq);

    List<YoloDTO> getYoloDTOByAttSeq(Long attSeq);
}
