package com.thedndpub.data.dte.deity;

public record DeitySymbolImageDte(
        String type,
        DeitySymbolImageHrefDte href,
        String credit,
        String title,
        Integer width,
        Integer height) {
}
