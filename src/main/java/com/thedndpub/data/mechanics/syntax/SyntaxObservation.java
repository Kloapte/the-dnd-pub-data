package com.thedndpub.data.mechanics.syntax;

public sealed interface SyntaxObservation permits FormulaSyntaxObservation, ModifierSyntaxObservation,
        LabelSyntaxObservation, OpaqueSyntaxObservation {
    MarkupTag tag();
}
