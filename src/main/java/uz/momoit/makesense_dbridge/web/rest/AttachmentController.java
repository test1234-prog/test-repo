package uz.momoit.makesense_dbridge.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import uz.momoit.makesense_dbridge.service.AttachmentService;
import uz.momoit.makesense_dbridge.service.LabelService;
import uz.momoit.makesense_dbridge.service.dto.*;
import uz.momoit.makesense_dbridge.domain.projection.ImageOfTaskResProjection;
import uz.momoit.makesense_dbridge.domain.projection.LabelOrdersProjection;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, exposedHeaders = "*",methods = {POST, GET, PUT, PATCH, DELETE, OPTIONS}, allowedHeaders = "*")
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Tag(name = "Attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    private final LabelService labelService;

    private final Logger log = LoggerFactory.getLogger(AttachmentController.class);

    /** return list of images by task
     *
     * @param imageOfTaskDTO
     * @return List of images by taskId
     */
    @GetMapping(value="/listOfImagesByTask")
    @Operation(summary = "List of images by task",
               description = "This method returns list of images are attached to the task")
    public List<ImageOfTaskResProjection> listOfImages(@Valid ImageOfTaskReqDTO imageOfTaskDTO) {
        log.debug("Rest request to get images by task. taskID:{}", imageOfTaskDTO.getDtlSeq());
        return attachmentService.getImagesOfTask(imageOfTaskDTO.getUserId(), imageOfTaskDTO.isQcCheck(), imageOfTaskDTO.getDtlSeq());
    }

    @PostMapping(value = "/save")
    @Operation(summary = "Save annotation of image",
               description = "To export annotation save to db")
    public void saveAnnotation(@Valid @RequestBody List<LabelDTO> labelDTOS, @RequestParam Long dtlSeq) {
        log.debug("Rest request export annotation to save to db");
        attachmentService.save(labelDTOS, dtlSeq);
    }

    @PostMapping(value = "/check-task")
    @Operation(summary = "to check task by inspector",
               description = "save result of checking task")
    public void checkTasks(@Valid @RequestBody List<CheckTaskDTO> checkTaskDTOS,
                           @RequestParam Long taskId,
                           @RequestParam String loginId,
                           @RequestParam String qcId) {
        log.debug("Rest request to check task");
        attachmentService.checkTask(checkTaskDTOS, taskId, loginId, qcId);
    }

    @GetMapping(value = "/import-annotation")
    @Operation(summary = "get data for import annotation",
               description = "get data YOLO format for import annotation")
    public List<LabelsImportDTO> importAnnotation(@RequestParam Long dtlSeq) {
        log.debug("Rest request to get files, taskId: {} ", dtlSeq);
        return labelService.convertLabelToYolo(dtlSeq);
    }

    @GetMapping(value = "/label-orders")
    @Operation(summary = "get labelName and labelOrders for import annotation by task Id",
               description = "get label orders")
    public List<LabelOrdersProjection> getLabelOrders(@RequestParam Long dtlSeq) {
        log.debug("Rest request to get files, taskId: {} ", dtlSeq);
        return labelService.getLabelOrders(dtlSeq);
    }
}
