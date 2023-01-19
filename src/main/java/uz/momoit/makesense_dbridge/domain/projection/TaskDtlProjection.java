package uz.momoit.makesense_dbridge.domain.projection;


public interface TaskDtlProjection {

    Long getDtlSeq();
    Long getEduSeq();
    String getLoginId();
    String getTaskDtlProg();
    String getTaskDtlStat();
    String getQcId();
}
