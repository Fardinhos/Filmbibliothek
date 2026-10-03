package com.gruppen.filmdatabase.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_chat_history")
@Data
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User firstUser;

    @ManyToOne
    private User secondUser;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages;


    public ChatHistory(Long id, User firstUser, User secondUser, List<Message> messages) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.messages = messages;
    }

    public ChatHistory(Long id, User firstUser, User secondUser) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.messages = new ArrayList<Message>();
    }

    public ChatHistory(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.messages = new ArrayList<Message>();
    }

    public ChatHistory() {
        this.messages = new ArrayList<Message>();
    }
}
