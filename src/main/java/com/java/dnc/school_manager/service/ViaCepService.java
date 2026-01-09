package com.java.dnc.school_manager.service;

import com.java.dnc.school_manager.dto.ViaCepResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    @Value("${viacep.url}")
    private String viaCepUrl;

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ViaCepResponse fetchAddress(String cep) {
        String cleanCep = cep.replaceAll("\\D", "");
        String url = viaCepUrl + cleanCep + "/json/";
        return restTemplate.getForObject(url, ViaCepResponse.class);
    }
}
