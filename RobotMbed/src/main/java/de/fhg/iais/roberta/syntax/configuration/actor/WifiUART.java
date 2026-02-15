package de.fhg.iais.roberta.syntax.configuration.actor;

import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.transformer.forClass.NepoConfiguration;
import de.fhg.iais.roberta.util.dbc.DbcException;

@NepoConfiguration(name = "WIFIUART", category = "CONFIGURATION_ACTOR",
        blocklyNames = {"robConf_wifiuart"})
public final class WifiUART extends ConfigurationComponent {
    private WifiUART() {
        super(null, null, null, null, null);
        throw new DbcException("should NEVER be called");
    }
}
