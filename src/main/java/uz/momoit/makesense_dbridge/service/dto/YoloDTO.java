package uz.momoit.makesense_dbridge.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YoloDTO {

    private Integer labelOrder;
    private Double yolo1; //(2* bbox_x + bbox_width )/2.0*(1/ image_width )
    private Double yolo2; //(2* bbox_y + bbox_height )/2.0*(1/ image_height)
    private Double yolo3; //(bbox_width )*(1/ image_width )
    private Double yolo4; //(bbox_height )*(1/ image_height)
}
