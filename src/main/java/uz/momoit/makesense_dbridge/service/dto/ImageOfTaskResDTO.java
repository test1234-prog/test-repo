package uz.momoit.makesense_dbridge.service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageOfTaskResDTO {

    private Integer attSeq;
    private Integer dtlSeq;
    private String name;
    private String path;
    private String ext;
    private Integer size;
    private String status;
    private String url;
}
