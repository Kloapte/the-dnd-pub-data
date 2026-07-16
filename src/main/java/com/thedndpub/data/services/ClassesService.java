package com.thedndpub.data.services;

import tools.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassWrapperDte;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class ClassesService {
    @Getter
    private final HashMap<String, ClassDte> classes;

    public ClassesService() {
        ClassWrapperDte druidWrapper = loadDruidClassFromJson();
        classes = new HashMap<>();

        for(ClassDte classDte: druidWrapper.getClasses()) {
            classes.put(classDte.getName() + " - " + classDte.getSource(), classDte);
        }

//        System.out.println(druidWrapper);
    }

    private ClassWrapperDte loadDruidClassFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data/classes/druid.json").getInputStream();
            return mapper.readValue(inputStream, ClassWrapperDte.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load druid.json", e);
        }
    }
}
