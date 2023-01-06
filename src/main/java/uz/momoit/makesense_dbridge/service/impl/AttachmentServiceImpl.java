package uz.momoit.makesense_dbridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.momoit.makesense_dbridge.domain.TaskCheckStatEnum;
import uz.momoit.makesense_dbridge.repository.AttachmentRepository;
import uz.momoit.makesense_dbridge.repository.LabelHistoryRepository;
import uz.momoit.makesense_dbridge.repository.LabelRepository;
import uz.momoit.makesense_dbridge.service.AttachmentService;
import uz.momoit.makesense_dbridge.service.TaskDtlDTO;
import uz.momoit.makesense_dbridge.service.dto.CheckTaskDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskResDTO;
import uz.momoit.makesense_dbridge.service.dto.LabelDTO;
import uz.momoit.makesense_dbridge.service.mapper.LabelHistoryMapper;
import uz.momoit.makesense_dbridge.service.mapper.LabelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    private final LabelRepository labelRepository;

    private final LabelHistoryRepository labelHistoryRepository;

    private final LabelMapper labelMapper;

    private final LabelHistoryMapper labelHistoryMapper;

    private final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);
    @Override
    public List<ImageOfTaskResDTO> getImagesOfTask(Long attId) {
        log.debug("Rest request to get images by taskId: {} ", attId);
        return attachmentRepository.getImagesByTask(attId);
    }

    @Override
    public void save(List<LabelDTO> labelDTOS, Long dtlSeq) {

        // delete labels from TB_LABEL_DATA
        List<Long> attSeqIds = labelDTOS.stream().map(x -> x.getAttSeq()).collect(Collectors.toList());
        attachmentRepository.deleteLabelData(attSeqIds);

        for(LabelDTO labelDTO : labelDTOS) {
          //1. insert or update tb_edu_result
          if(!(attachmentRepository.checkEduResult(labelDTO.getAttSeq()) > 0)) {
              //insert
              TaskDtlDTO taskDtl = attachmentRepository.getTaskDtl(labelDTO.getAttSeq());
              attachmentRepository.insertEduResult(taskDtl.getLoginId(), taskDtl.getEduSeq(), taskDtl.getDtlSeq(), labelDTO.getAttSeq(), "OK", "0","1");
          }
          //2. insert or update tb_label_data
              //delete all label
              attachmentRepository.insertLabelData(labelDTO.getAttSeq(),labelDTO.getLabelName(),labelDTO.getLabelOrder(),labelDTO.getBboxX(),labelDTO.getBboxY(),labelDTO.getBboxWidth(),labelDTO.getBboxWidth(), labelDTO.getImgWidth(), labelDTO.getImgHeight());
        }
        //3. update TB_TASK_DTL
        attachmentRepository.updateTaskDtlProg(dtlSeq);
        attachmentRepository.updateTaskDtlStatus(dtlSeq);
    }

    @Override
    public void checkTask(List<CheckTaskDTO> checkTaskDTOS, Long taskId) {
        // get point from TB_EDU_MST.POINT (the point is given for each image in the same value)
        Long point = attachmentRepository.getPointByTaskId(taskId);
        int VRIFYSTTUS;
        //1. update TB_EDU_RESULT
        for(CheckTaskDTO checkTaskDTO : checkTaskDTOS) {
            if(checkTaskDTO.getTaskCheckStatEnum() == TaskCheckStatEnum.OK) {
                VRIFYSTTUS = 2;
                //insert data to table TB_POINT
                attachmentRepository.updateTbPoint(checkTaskDTO.getLoginId(), point, LocalDateTime.now());
                attachmentRepository.updateEduResult(VRIFYSTTUS, checkTaskDTO.getAttSeq(), checkTaskDTO.getQcId(), point,LocalDateTime.now());
            }
            else {
                VRIFYSTTUS = 3;
                labelRepository
                        .getLabelsByAttSeq(checkTaskDTO.getAttSeq())
                        .stream().map(label -> labelMapper.toDto(label))
                        .map(labelDto -> labelHistoryMapper.toEntity(labelDto))
                        .map(labelHistoryRepository::save);
                attachmentRepository.updateEduResult(VRIFYSTTUS, checkTaskDTO.getAttSeq(), checkTaskDTO.getQcId(), 0L, LocalDateTime.now());
            }
        }

        //2. Update TB_TASK_DTL (All images approved TASK_DTL_STAT = 4, at least one rejected TASK_DTL_STAT = 5)
        int dtlSeq=1;
        attachmentRepository.checkApprovedImagesOfTask(dtlSeq);

        //3. Inset TB_EDU_HISTORY(All images approved STATUS = 2, at least one rejected STATUS = 3)

    }
}
