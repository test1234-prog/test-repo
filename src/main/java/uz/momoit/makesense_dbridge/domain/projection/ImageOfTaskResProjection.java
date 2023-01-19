package uz.momoit.makesense_dbridge.domain.projection;

public interface ImageOfTaskResProjection {
    Integer getAttSeq();
    Long getDtlSeq();
    String getPath();
    Long getSize();
    String getName();
    String getExt();
    String getStatus();
    default String getUrl() {
        return String.format(
                "%s%s%s%s",
                "https://",
                "makesense-s",//TODO dynamic
                ".s3.amazonaws.com",
                getPath());
    }
}
