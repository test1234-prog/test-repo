package uz.momoit.makesense_dbridge.service.dto;

public interface ImageOfTaskResDTO {
    Integer getAttSeq();
    Long getDtlSeq();
    String getPath();
    Long getSize();
    String getName();
    String getExt();
    String getStatus();

//    default String getPath() { // TODO remove after remove working makesense-s bucket
//        String path2 = null;
//        if(getAttSeq()==1) {
//            path2 = "https://olma1.s3.amazonaws.com/img1.jpg";
//        } else if (getAttSeq() == 2) {
//            path2 = "https://olma1.s3.amazonaws.com/img2.jpg";
//        } else if(getAttSeq() == 3) {
//            path2 = "https://olma1.s3.amazonaws.com/img3.jpg";
//        }
//        return path2;
//    }
    default String getUrl() {
        return String.format(
                "%s%s%s%s",
                "https://",
                "makesense-s",//TODO dynamic
                ".s3.amazonaws.com",
                getPath());
    }
}
