package uz.momoit.makesense_dbridge.web.rest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.momoit.makesense_dbridge.service.AttachmentService;
import uz.momoit.makesense_dbridge.service.dto.CheckTaskDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskReqDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskResDTO;
import uz.momoit.makesense_dbridge.service.dto.LabelDTO;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

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

    @GetMapping(
            value = "/import-annotation",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void importAnnotation(Long loginId, Long qcId, Long taskId, List<HttpServletResponse> responses) throws IOException {
        log.debug("Rest request to get files, taskId: {} ", taskId);
        String myString = "Hello";
        HttpServletResponse response = null;
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment;filename=myFile.txt");
        ServletOutputStream out = response.getOutputStream();
        out.println(myString);
        out.flush();
        out.close();

        HttpServletResponse response1 = null;
        response1.setContentType("text/plain");
        response1.setHeader("Content-Disposition","attachment;filename=myFile.txt");
        ServletOutputStream out1= response.getOutputStream();
        out1.println(myString);
        out1.flush();
        out1.close();

        responses.add(response);
        responses.add(response1);
    }
}
