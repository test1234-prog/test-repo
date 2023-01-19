package uz.momoit.makesense_dbridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.momoit.makesense_dbridge.domain.enumeration.TaskCheckStatEnum;
import uz.momoit.makesense_dbridge.repository.*;
import uz.momoit.makesense_dbridge.service.AttachmentService;
import uz.momoit.makesense_dbridge.service.dto.*;
import uz.momoit.makesense_dbridge.service.mapper.LabelHistoryMapper;
import uz.momoit.makesense_dbridge.service.mapper.LabelMapper;
import uz.momoit.makesense_dbridge.web.rest.errors.BadRequestAlertException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    private final LabelRepository labelRepository;

    private final LabelHistoryRepository labelHistoryRepository;

    private final EduResultRepository eduResultRepository;

    private final EduResultHistoryRepository eduResultHistoryRepository;

    private final TaskDtlRepository taskDtlRepository;

    private final LabelMapper labelMapper;

    private final LabelHistoryMapper labelHistoryMapper;

    @Value("${aws.bucket}")
    private  String BUCKET_NAME;

    private final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);
    @Override
    public List<ImageOfTaskResDTO> getImagesOfTask(String userId, Boolean qcCheck, Long attId) {
        log.debug("Rest request to get images by taskId: {} ", attId);
        String qcChk = qcCheck ? "Y" : "N";
        return attachmentRepository.getImagesByTask(userId, qcChk, attId);
    }

    private String generateUrl(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(BUCKET_NAME);
        sb.append(".amazonaws.com");
        sb.append(path);
        return sb.toString();
    }

    @Override
    public void save(List<LabelDTO> labelDTOS, Long dtlSeq) {
        log.debug("Rest request to save labels with dtlSeq: {} ", dtlSeq);
        List<Long> attSeqIds = labelDTOS.stream().map(LabelDTO::getAttSeq).collect(Collectors.toList());
        //check if image is approved , it is not edit
        if(eduResultRepository.checkExistsApprovedImage(attSeqIds) > 0) {
            throw new BadRequestAlertException("Image is approved, it is not edit", "Attachment", "imageApproved");
        }

        // save TB_LABEL_DATA_HISTORY table
        for (Long attSeqId : attSeqIds) {
            labelHistoryRepository.saveLabelHistory(attSeqId);
        }
        // delete labels from TB_LABEL_DATA table
        labelRepository.deleteLabelData(attSeqIds);

        for(LabelDTO labelDTO : labelDTOS) {
          //1. insert or update tb_edu_result
          if(eduResultRepository.checkEduResult(labelDTO.getAttSeq()) == 0) {
              //insert
              TaskDtlDTO taskDtl = attachmentRepository.getTaskDtl(labelDTO.getAttSeq());
              eduResultRepository.insertEduResult(taskDtl.getLoginId(), taskDtl.getEduSeq(), taskDtl.getDtlSeq(), labelDTO.getAttSeq(), "OK", "0","1");
          }

        //2.get label order
        Long aLong = labelRepository.getLabelOrderIdByAttSeqAndName(labelDTO.getAttSeq())
                .stream()
                .filter(x -> x.getLabelName().equals(labelDTO.getLabelName())).map(LabelOrdersDTO::getLabelOrder)
                .findFirst()
                .orElse(
                    labelRepository.getLabelOrderIdByAttSeqAndName(labelDTO.getAttSeq())
                            .stream()
                            .map(LabelOrdersDTO::getLabelOrder)
                            .max(Long::compareTo).map(x2->x2+1).orElse(0L)
                );
        //3. insert or update tb_label_data
        labelRepository.insertLabelData(labelDTO.getAttSeq(),labelDTO.getLabelName(),aLong,labelDTO.getBboxX(),labelDTO.getBboxY(),labelDTO.getBboxWidth(),labelDTO.getBboxHeight(), labelDTO.getImgWidth(), labelDTO.getImgHeight());
        }
        //3. update TB_TASK_DTL
        taskDtlRepository.updateTaskDtlProg(dtlSeq);
        taskDtlRepository.updateTaskDtlStatus(dtlSeq);
        log.debug("Successfully saved labels with dtlSeq: {} ", dtlSeq);
    }

    @Override
    public void checkTask(List<CheckTaskDTO> checkTaskDTOS, Long taskId, String loginId, String qcId) {
        log.debug("Rest request to check task with taskId: {} ", taskId);
        TaskDtlDTO taskDtlDTO = taskDtlRepository.getTaskDtlByDtlSeq(taskId);
        if(taskDtlDTO == null) {
            throw new BadRequestAlertException("Task not found", "Task", "taskNotFound");
        }
        if(!taskDtlDTO.getQcId().equals(qcId.toLowerCase())) {
            throw new BadRequestAlertException("Task is not allowed to check  by this Inspector!", "Task", "checkNotAllowedTask");
        }
        //check if image is approved , it is not edit
        List<Long> attSeqList = checkTaskDTOS.stream().map(CheckTaskDTO::getAttSeq).collect(Collectors.toList());
        if(eduResultRepository.checkExistsApprovedImage(attSeqList) > 0) {
            throw new BadRequestAlertException("Image is approved, it is not allowed to check", "Attachment", "notCheckApprovedImage");
        }

        // get point from TB_EDU_MST.POINT (the point is given for each image in the same value)
        Long point = attachmentRepository.getPointByTaskId(taskId);
        int VRIFYSTTUS;
        //1. update TB_EDU_RESULT
        for(CheckTaskDTO checkTaskDTO : checkTaskDTOS) {
            //when "OK" button clicked
            if(checkTaskDTO.getTaskCheckStatEnum() == TaskCheckStatEnum.OK) {
                VRIFYSTTUS = 2;
                //insert data to table TB_POINT
                attachmentRepository.updateTbPoint(loginId, point, LocalDateTime.now());
                eduResultRepository.updateEduResult(VRIFYSTTUS, checkTaskDTO.getAttSeq(), qcId, point,LocalDateTime.now());
            }
            //when "REJECTED" button clicked
            else {
                VRIFYSTTUS = 3;

                //all label values belong to image to insert TB_LABEL_DATA_HISTORY table
                labelHistoryRepository.saveLabelHistory(checkTaskDTO.getAttSeq());
                //TODO TB_EDU_RESULT_HISTORY
                eduResultHistoryRepository.savedEduResultHistory(checkTaskDTO.getAttSeq());
                eduResultRepository.updateEduResult(VRIFYSTTUS, checkTaskDTO.getAttSeq(), qcId, 0L, LocalDateTime.now());
            }
        }

        //2. Update TB_TASK_DTL (All images approved TASK_DTL_STAT = 4, at least one rejected TASK_DTL_STAT = 5)
        taskDtlRepository.checkApprovedImagesOfTask(taskId);

        //3. Inset TB_EDU_RESULT(All images approved VRIFYSTTUS = 2, at least one rejected VRIFYSTTUS = 3)
        log.debug("successfully checked task with taskId: {} ", taskId);
    }
}
