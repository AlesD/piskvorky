package cz.doleckovi.piskvorky.core.search;

public record PatternClass(
        String name,
        int stoneCount,
        boolean terminal,
        boolean searchExtension
) {}
