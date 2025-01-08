package com.example.loanmanagerapi.MessageQueue;

import lombok.Data;

@Data
public class Message {
    private String id;
    private String email;
    private String content;
    private String timestamp;
    private String Type;
}
