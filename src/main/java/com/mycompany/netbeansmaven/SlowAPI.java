package com.mycompany.netbeansmaven;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 *
 * @author scheng
 */
interface SlowAPI {
    CompletionStage<Optional<String>> read(final String key);
}
