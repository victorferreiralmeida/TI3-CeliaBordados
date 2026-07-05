package com.celiabordados.service;

import com.celiabordados.Personalizacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalizacaoService {
    
    @Autowired
    private PersonalizacaoRepository personalizacaoRepository;

    PersonalizacaoService(PersonalizacaoRepository personalizacaoRepository) {
        this.personalizacaoRepository = personalizacaoRepository;
    }
    
    public Personalizacao savePersonalizacao(Personalizacao personalizacao ) {
        return personalizacaoRepository.save(personalizacao);
    }

}
