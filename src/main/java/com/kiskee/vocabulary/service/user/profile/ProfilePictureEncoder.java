package com.kiskee.vocabulary.service.user.profile;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class ProfilePictureEncoder {

    public String encodeWithBase64(String pictureLink) {
        RestTemplate restTemplate = new RestTemplate();

        byte[] imageBytes = restTemplate.getForObject(pictureLink, byte[].class);

        // data:image/png;base64, + base64 encoded image
        return Base64.getEncoder().encodeToString(imageBytes);
    }

}