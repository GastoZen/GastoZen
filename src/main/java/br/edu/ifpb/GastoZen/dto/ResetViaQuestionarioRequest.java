package br.edu.ifpb.GastoZen.dto;

import lombok.Data;

@Data
public class ResetViaQuestionarioRequest {
    private String email;
    private Integer questionId;
    private String answer;
    private String newPassword;
}
