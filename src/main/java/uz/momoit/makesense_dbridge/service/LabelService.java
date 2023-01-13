package uz.momoit.makesense_dbridge.service;

import uz.momoit.makesense_dbridge.service.dto.LabelOrdersDTO;
import uz.momoit.makesense_dbridge.service.dto.LabelsImportDTO;

import java.util.List;

public interface LabelService {
    List<LabelsImportDTO> convertLabelToYolo(Long dtlSeq);

    List<LabelOrdersDTO> getLabelOrders(Long dtlSeq);
}
