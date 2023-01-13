package uz.momoit.makesense_dbridge.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class LabelDTO {
    private Long labelSeq;
    @NotNull
    private Long attSeq;
    @NotNull
    private String labelName;
    private int labelOrder;
    @NotNull
    private int bboxX;
    @NotNull
    private int bboxY;
    @NotNull
    private int bboxWidth;
    @NotNull
    private int bboxHeight;
    @NotNull
    private int imgWidth;
    @NotNull
    private int imgHeight;
}
