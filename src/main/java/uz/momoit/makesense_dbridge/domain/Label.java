package uz.momoit.makesense_dbridge.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_LABEL_DATA")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lb_sq")
    private Long labelSeq;

    @Column(name = "att_seq")
    private Long attSeq;

    @Column(name = "label_name")
    private String labelName;

    @Column(name = "label_order")
    private int labelOrder;

    @Column(name = "bbox_x")
    private Double bboxX;

    @Column(name = "bbox_y")
    private Double bboxY;

    @Column(name = "bbox_width")
    private Double bboxWidth;

    @Column(name = "bbox_height")
    private Double bboxHeight;

    @Column(name = "img_width")
    private Double imgWidth;

    @Column(name = "img_height")
    private Double imgHeight;
}
