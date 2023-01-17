package uz.momoit.makesense_dbridge.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.momoit.makesense_dbridge.domain.enumeration.TaskCheckStatEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckTaskDTO {
    private Long loginId;
    private Long attSeq;
    private Long qcId;
    private TaskCheckStatEnum taskCheckStatEnum;
}
