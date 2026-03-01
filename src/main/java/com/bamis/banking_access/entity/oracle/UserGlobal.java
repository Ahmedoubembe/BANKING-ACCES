package com.bamis.banking_access.entity.oracle;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERGLOBAL")
public class UserGlobal {

    @Id
    @Column(name = "LOGIN", nullable = false, length = 40)
    private String login;

    @Column(name = "PWD", nullable = false, length = 400)
    private String pwd;

    @Column(name = "PRENOM", length = 40)
    private String prenom;

    @Column(name = "NOM", length = 40)
    private String nom;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;
}