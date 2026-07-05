package com.celiabordados.controller;

import com.celiabordados.Personalizacao;
import com.celiabordados.service.PersonalizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/personalizacoes")
@CrossOrigin(origins = "*")
public class PersonalizacaoController {

    @Autowired
    private PersonalizacaoService personalizacaoService;

    @PostMapping
    public ResponseEntity<Personalizacao> createPersonalizacao(@RequestBody Personalizacao personalizacao) {
        Personalizacao savedPersonalizacao = personalizacaoService.savePersonalizacao(personalizacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPersonalizacao);
    }

}
