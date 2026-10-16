package com.thedndpub.data.dte.background;

import java.util.List;

public record BackgroundPrerequisiteDte(List<String> campaign) {
    public BackgroundPrerequisiteDte {
        campaign = List.copyOf(campaign);
    }
}
