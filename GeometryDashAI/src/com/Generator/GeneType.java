package com.Generator;

public enum GeneType {
    EMPTY,              // Pusta przestrzeń
    BLOCK_GROUND,       // Blok na ziemi
    SPIKE_GROUND,       // Kolec na ziemi
    BLOCK_FLOATING,     // Unoszący się blok (wymagałby parametru wysokości)
    JUMP_PAD,           // Skocznia
    THREE_SPIKES_GROUND, // Grupa trzech kolców
    SPIKE_AIR,
    BLOCK_AIR
}
