package com.example.personalityquest.Model.quiz;

/** Holds an archetype row loaded from the catalog
 * @param archetypeId the archetype ID
 * @param name the archetype name
 * @param smallDescription the short description
 * @param longDescription the full description
 * @param strengths the archetype strengths
 * @param weaknesses the archetype weaknesses
 * @param valueMean the balanced value
 * @param valueMeanDefinition the balanced-value description
 * @param valueDeficit the deficient value
 * @param valueDeficitDefinition the deficient-value description
 * @param valueExcess the excessive value
 * @param valueExcessDefinition the excessive-value description
 */
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