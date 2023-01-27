package uz.momoit.makesense_dbridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.momoit.makesense_dbridge.domain.enumeration.TaskCheckStatEnum;
import uz.momoit.makesense_dbridge.repository.*;
import uz.momoit.makesense_dbridge.service.AttachmentService;
import uz.momoit.makesense_dbridge.service.LabelService;
import uz.momoit.makesense_dbridge.service.dto.*;
import uz.momoit.makesense_dbridge.domain.projection.LabelOrdersProjection;
import uz.momoit.makesense_dbridge.domain.projection.TaskDtlProjection;
import uz.momoit.makesense_dbridge.web.rest.errors.BadRequestAlertException;

import javax.persistence.Tuple;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    private final LabelRepository labelRepository;

    private final LabelService labelService;

    private final LabelHistoryRepository labelHistoryRepository;

    private final EduResultRepository eduResultRepository;

    private final EduResultHistoryRepository eduResultHistoryRepository;

    private final TaskDtlRepository taskDtlRepository;

    private final PointRepository pointRepository;

    private final EduMstRepository eduMstRepository;


    @Value("${aws.bucket}")
    private  String BUCKET_NAME;

    private final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);
    @Override
    public List<ImageOfTaskResDTO> getImagesOfTask(String userId, Boolean qcCheck, Long attId) {
        log.debug("Rest request to get images by taskId: {} ", attId);
        String qcChk = qcCheck ? "Y" : "N";
        List<Tuple> imagesByTask = attachmentRepository.getImagesByTask(userId, qcChk, attId);
        return imagesByTask.stream().map(tuple -> {
            ImageOfTaskResDTO imageOfTaskResDTO = new ImageOfTaskResDTO();
            imageOfTaskResDTO.setAttSeq(tuple.get(0, Integer.class));
            imageOfTaskResDTO.setDtlSeq(tuple.get(1, Integer.class));
            imageOfTaskResDTO.setName(tuple.get(2, String.class));
            imageOfTaskResDTO.setPath(tuple.get(3, String.class));
            imageOfTaskResDTO.setExt(tuple.get(4, String.class));
            imageOfTaskResDTO.setSize(tuple.get(5, Integer.class));
            imageOfTaskResDTO.setStatus(tuple.get(6, String.class));
            imageOfTaskResDTO.setUserId(tuple.get(7, String.class));
            imageOfTaskResDTO.setQcId(tuple.get(8, String.class));
            imageOfTaskResDTO.setUrl(getUrl(tuple));
            return imageOfTaskResDTO;
        }).collect(Collectors.toList());
    }

    private String getUrl(Tuple tuple) {
        return "https://" + BUCKET_NAME + ".s3.amazonaws.com" + tuple.get(3, String.class);
    }

    @Override
    public void save(List<LabelDTO> labelDTOS, Long dtlSeq) {
        log.debug("Rest request to save labels with dtlSeq: {} ", dtlSeq);
        List<Long> attSeqIds = labelDTOS.stream().map(LabelDTO::getAttSeq).collect(Collectors.toList());
        //check if image is approved , it is not edit
        if(eduResultRepository.checkExistsApprovedImage(attSeqIds) > 0) {
            throw new BadRequestAlertException("Image is approved, it is not edit", "Attachment", "imageApproved");
        }
        List<Integer> rejectedImageIds = eduResultRepository.getRejectedImageIds(dtlSeq);

        // save TB_LABEL_DATA_HISTORY table
        for (Long attSeqId : attSeqIds) {
            labelHistoryRepository.saveLabelHistory(attSeqId);
        }
        // delete labels from TB_LABEL_DATA table
        labelRepository.deleteLabelData(attSeqIds);

        for(LabelDTO labelDTO : labelDTOS) {
          //1. insert or update tb_edu_result
          String VRIFYSTTUS;
          if(eduResultRepository.checkEduResult(labelDTO.getAttSeq()) == 0) {
              //insert
              TaskDtlProjection taskDtl = taskDtlRepository.getTaskDtl(labelDTO.getAttSeq());
              eduResultRepository.insertEduResult(taskDtl.getLoginId(), taskDtl.getEduSeq(), taskDtl.getDtlSeq(), labelDTO.getAttSeq(), "OK", "0","1");
          } else if(rejectedImageIds.contains(labelDTO.getAttSeq())) {
              eduResultRepository.updateEduResult(4,labelDTO.getAttSeq(), null, 0L, null);// reworked labelled image after rejected image
          }

        //2. insert or update tb_label_data
        labelRepository.insertLabelData(labelDTO.getAttSeq(),labelDTO.getLabelName(),-1L,labelDTO.getBboxX(),labelDTO.getBboxY(),labelDTO.getBboxWidth(),labelDTO.getBboxHeight(), labelDTO.getImgWidth(), labelDTO.getImgHeight());
        }

        //update label_order in TB_LABEL_DATA
        labelService.updateLabelOrders(dtlSeq);
        //3. update TB_TASK_DTL
        taskDtlRepository.updateTaskDtlProg(dtlSeq);
        taskDtlRepository.updateTaskDtlStatus(dtlSeq); //after saved label TB_TASK_DETAIL status is changed to all task is done then status = 3 else 2
        log.debug("Successfully saved labels with dtlSeq: {} ", dtlSeq);
    }

    @Override
    public void checkTask(List<CheckTaskDTO> checkTaskDTOS, Long taskId, String loginId, String qcId) {
        log.debug("Rest request to check task with taskId: {} ", taskId);
        TaskDtlProjection taskDtlDTO = taskDtlRepository.getTaskDtlByDtlSeq(taskId);
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
        Long point = eduMstRepository.getPointByTaskId(taskId);
        int VRIFYSTTUS;
        //1. update TB_EDU_RESULT
        for(CheckTaskDTO checkTaskDTO : checkTaskDTOS) {
            //when "OK" button clicked
            if(checkTaskDTO.getTaskCheckStatEnum() == TaskCheckStatEnum.APPROVED) {
                VRIFYSTTUS = 2;
                //insert data to table TB_POINT
                pointRepository.updateTbPoint(loginId, point, LocalDateTime.now());
                eduResultRepository.updateEduResult(VRIFYSTTUS, checkTaskDTO.getAttSeq(), qcId, point,LocalDateTime.now());
            }
            //when "REJECTED" button clicked
            else {
                VRIFYSTTUS = 3;

                //all label values belong to image to insert TB_LABEL_DATA_HISTORY table
                labelHistoryRepository.saveLabelHistory(checkTaskDTO.getAttSeq());
                eduResultHistoryRepository.savedEduResultHistory(checkTaskDTO.getAttSeq());
                eduResultRepository.updateEduResult(VRIFYSTTUS, checkTaskDTO.getAttSeq(), qcId, 0L, LocalDateTime.now());
            }
        }

        //2. update TB_TASK_DTL table because, if image is rejected, TASK_DTL_PROG colum value will change
        taskDtlRepository.updateTaskDtlProg(taskId);
        //3. Update TB_TASK_DTL (All images approved TASK_DTL_STAT = 4, at least one rejected TASK_DTL_STAT = 5)
        taskDtlRepository.checkApprovedImagesOfTask(taskId);


        log.debug("successfully checked task with taskId: {} ", taskId);
    }

    @Override
    public void createFileForImportAnnotation(HttpServletResponse response, Long attSeq) throws IOException {
        log.debug("Rest request to create file for import annotation with attSeq: {} ", attSeq);
        List<YoloDTO> yoloDTOByAttSeq = labelService.getYoloDTOByAttSeq(attSeq);
        if (yoloDTOByAttSeq.size() == 0) {
            throw new BadRequestAlertException("No data to export", "Label", "noDataToExport");
        }
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename="+getFileName(attSeq)+".txt");
        ServletOutputStream out  = response.getOutputStream();
        for(YoloDTO yoloDTO : yoloDTOByAttSeq) {
            out.println(yoloDTO.getLabelOrder() + " " + yoloDTO.getYolo1() + " " + yoloDTO.getYolo2() + " " + yoloDTO.getYolo3() + " " + yoloDTO.getYolo4());
        }
        out.flush();
        out.close();
    }

    @Override
    public String getFileName(Long attSeq) {
        if (attachmentRepository.getFileNameByAttSeq(attSeq) != null) {
            String fileName = attachmentRepository.getFileNameByAttSeq(attSeq);
            int index = fileName.indexOf(".");
            return fileName.substring(0, index);
        }
        return null;
    }

    @Override
    public void createLabels(HttpServletResponse response, Long dtlSeq) throws IOException {
        log.debug("Rest request to create labels with dtlSeq: {} ", dtlSeq);
        List<LabelOrdersProjection> labelOrders = labelService.getLabelOrders(dtlSeq);
        if(labelOrders.size() == 0) {
            throw new BadRequestAlertException("No data to export", "Label", "noDataToExport");
        }
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=labels.txt");
        ServletOutputStream out  = response.getOutputStream();
        for(LabelOrdersProjection label : labelOrders) {
            out.println(label.getLabelName());
        }
        out.flush();
        out.close();
    }
}
