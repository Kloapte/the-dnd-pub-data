package com.thedndpub.data.controllers;

import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.services.ClassesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassesController {
    private final ClassesService classesService;
    
    @GetMapping()
    public List<ClassDte> getRaces() {
        return this.classesService.getClasses().values().stream().toList();
    }
}
