package uz.momoit.makesense_dbridge.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.momoit.makesense_dbridge.service.S3Service;

import java.io.File;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AWSController {

    private final S3Service s3Service;

    private final Logger log = LoggerFactory.getLogger(AWSController.class);

    @Value("${aws.bucket}")
    private  String BUCKET_NAME;

    /** Get object from AWS S3 file storage
     *
     * @param objectName (PATH + NAME columns table of TB_ATT)
     * @return This method return File object from AWS S3
     */
//    @GetMapping(value = "/object")
//    @Operation(summary = "get image from AWS S3 file storage",
//               description = "This method return image as file"
//    )
//    public File downloadObject(@RequestParam String objectName) {
//        log.debug("Rest request to get image from AWS S3 objectName: {}", objectName);
//        s3Service.downloadObject(BUCKET_NAME, objectName);
//        return new File("./" + objectName);
//    }
}
