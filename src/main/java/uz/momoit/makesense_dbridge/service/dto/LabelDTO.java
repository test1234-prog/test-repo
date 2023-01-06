package uz.momoit.makesense_dbridge.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LabelDTO {
    private Long labelSeq;
    private Long attSeq;
    private String labelName;
    private int labelOrder;
    private int bboxX;
    private int bboxY;
    private int bboxWidth;
    private int bboxHeight;
    private int imgWidth;
    private int imgHeight;
}
