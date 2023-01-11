package uz.momoit.makesense_dbridge.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelsImportDTO {

    private Long attSeq;

    private String vrifysttus;

    List<YoloDTO> yoloDTOS;
}
