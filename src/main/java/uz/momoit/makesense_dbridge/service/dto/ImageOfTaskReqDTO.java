package uz.momoit.makesense_dbridge.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class ImageOfTaskReqDTO {

    @NotNull
    String userId;
    @NotNull
    boolean qcCheck;
    @NotNull
    Long dtlSeq;
}
