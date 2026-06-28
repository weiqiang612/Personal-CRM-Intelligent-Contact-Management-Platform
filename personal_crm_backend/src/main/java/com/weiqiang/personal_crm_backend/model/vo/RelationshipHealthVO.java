package com.weiqiang.personal_crm_backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View object for relationship health statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipHealthVO {
    private Long active;
    private Long followUp;
    private Long inactive;
    private Long noActivity;
    private Long total;

    private List<ContactHealthItemVO> activeList;
    private List<ContactHealthItemVO> followUpList;
    private List<ContactHealthItemVO> inactiveList;
    private List<ContactHealthItemVO> noActivityList;

    public RelationshipHealthVO(Long active, Long followUp, Long inactive, Long noActivity, Long total) {
        this.active = active;
        this.followUp = followUp;
        this.inactive = inactive;
        this.noActivity = noActivity;
        this.total = total;
    }
}
