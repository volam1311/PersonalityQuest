package com.example.personalityquest.Model.quiz;

public record ArchetypeRecord(
        int archetypeId,
        String name,
        String smallDescription,
        String longDescription,
        String strengths,
        String weaknesses,
        String valueMean,
        String valueMeanDefinition,
        String valueDeficit,
        String valueDeficitDefinition,
        String valueExcess,
        String valueExcessDefinition) {
}