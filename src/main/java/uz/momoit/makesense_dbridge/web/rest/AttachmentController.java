package uz.momoit.makesense_dbridge.web.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.momoit.makesense_dbridge.service.AttachmentService;
import uz.momoit.makesense_dbridge.service.LabelService;
import uz.momoit.makesense_dbridge.service.dto.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, exposedHeaders = "*",methods = {POST, GET, PUT, PATCH, DELETE, OPTIONS}, allowedHeaders = "*")
@RequestMapping(value = "/api")
@RequiredArgsConstructor
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
    public List<ImageOfTaskResDTO> listOfImages(@Valid ImageOfTaskReqDTO imageOfTaskDTO) {
        log.debug("Rest request to get images by task. taskID:{}", imageOfTaskDTO.getDtlSeq());
        return attachmentService.getImagesOfTask(imageOfTaskDTO.getUserId(), imageOfTaskDTO.getDtlSeq());
    }

    @PostMapping(value = "/save")
    public void saveAnnotation(@Valid @RequestBody List<LabelDTO> labelDTOS, @RequestParam Long dtlSeq) {
        log.debug("Rest request export annotation to save to db");
        attachmentService.save(labelDTOS, dtlSeq);
    }

    @PostMapping(value = "/check-task")
    public void checkTasks(@Valid @RequestBody List<CheckTaskDTO> checkTaskDTOS, @RequestParam Long taskId) {
        log.debug("Rest request to check task");
        attachmentService.checkTask(checkTaskDTOS, taskId);
    }

    @GetMapping(value = "/import-annotation")
    public List<LabelsImportDTO> importAnnotation(@RequestParam Long dtlSeq) {
        log.debug("Rest request to get files, taskId: {} ", dtlSeq);
        return labelService.convertLabelToYolo(dtlSeq);
    }
}
